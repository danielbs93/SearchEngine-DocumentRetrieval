package Index;

import Rules.Token;
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;
//import edu.stanford.nlp.util.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class Indexer {

    private ExecutorService threadPoolExecutor;
    private String CorpusPath;
    private String SavingPostingFilePath;
    private boolean isStemmer;
    private ConcurrentHashMap<Token, MutablePair<Integer, Integer>> Dictionary;//term-df-termID
    private ConcurrentHashMap<Token, ArrayList<Integer>> EntitiesDictionary;//term-df-termID-currentDocID
    private int NumOfFilesToRead;
    private boolean isActive;
    private AtomicInteger TermID;
    private AtomicInteger DocID;
    private AtomicInteger FileID;
    private AtomicInteger TempPosingFileName;
    private int fromWhereToRead;
    private Comparator<String[]> myComparator;
    private int Intervals;

    public Indexer() {
        myComparator = (o1, o2) -> {
            int termIdComparing = Integer.compare(Integer.valueOf(o1[0]), Integer.valueOf(o2[0]));
            if (termIdComparing != 0)
                return termIdComparing;
            int docIdComparing = Integer.compare(Integer.valueOf(o1[1]), Integer.valueOf(o2[1]));
            return docIdComparing;
        };
    }

    public Indexer(String corpusPath, String savingPostingFilePath
            , boolean stemmer, int poolSize, int from, int numOfFilesToRead
            , ConcurrentHashMap<Token, MutablePair<Integer, Integer>> dictionary
            , ConcurrentHashMap<Token, ArrayList<Integer>> entitiesDictionary) {
        CorpusPath = corpusPath;
        SavingPostingFilePath = savingPostingFilePath;
        isStemmer = stemmer;
        Dictionary = dictionary;
        EntitiesDictionary = entitiesDictionary;
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        TermID = new AtomicInteger(0);
        DocID = new AtomicInteger(0);
        FileID = new AtomicInteger(0);
        TempPosingFileName = new AtomicInteger(0);
        NumOfFilesToRead = numOfFilesToRead;
        fromWhereToRead = from;
        myComparator = (o1, o2) -> {
            int termIdComparing = Integer.compare(Integer.valueOf(o1[0]), Integer.valueOf(o2[0]));
            if (termIdComparing != 0)
                return termIdComparing;
            int docIdComparing = Integer.compare(Integer.valueOf(o1[1]), Integer.valueOf(o2[1]));
            return docIdComparing;
        };
    }

    /**
     * This function responsible for parsing the text and indexing it into a temporary posting files
     */
    public void Index() {
        isActive = true;
        File[] files = (new File(CorpusPath)).listFiles();
        int increment = 0;
        if (Intervals == 0)
            increment = 1;
        else
            increment = Intervals;
        for (int i = fromWhereToRead; i < NumOfFilesToRead - Intervals; i += increment) {
            ReadFile fileReader = new ReadFile(files, i, i + increment, increment);
            threadPoolExecutor.execute(() -> {
                StringBuilder docLexiconData = new StringBuilder();
                StringBuilder IntervalsFilesData = new StringBuilder();
                while (!fileReader.isEmpty()) {
                    String Doc = fileReader.getNextDoc();
                    String Text = ExtractText(Doc);
                    if (Text.length() > 0) {
                        Parser parser = new Parser(Text, CorpusPath, isStemmer);
                        ArrayList<Token>[] tokenList = parser.Parse(true);
                        Integer[] maxTFandUniqueTerms = new Integer[2];
                        maxTFandUniqueTerms[0] = 0;
                        maxTFandUniqueTerms[1] = 0;
                        int DocLength = tokenList[0].size() + tokenList[1].size();
                        String DocNo = fileReader.getDocNO();
                        synchronized (DocID) {
                            StringBuilder docData = new StringBuilder();
                            for (Token term : tokenList[0]) {
                                if (term != null) {
                                    if (!Dictionary.containsKey(term)) {
                                        if (UpdateTermInfo(tokenList[0], term, true, docData, maxTFandUniqueTerms))//including mapping termID
                                            TermID.incrementAndGet();
                                    } else
                                        UpdateTermInfo(tokenList[0], term, false, docData, maxTFandUniqueTerms);
                                    if (term.getTf() > maxTFandUniqueTerms[0])
                                        maxTFandUniqueTerms[0] = term.getTf();
                                }
                            }
                            UpdateEntitiesInfo(tokenList, maxTFandUniqueTerms, docData);
                            mergeTerms(docData);
                            if (!fileReader.DecreaseDocCounter()) {
                                FileID.incrementAndGet();
                            }
                            docLexiconData.append(DocID.get() + ";" + DocNo + ";" + fileReader.getFileNO() + ";" + maxTFandUniqueTerms[0] + ";" + maxTFandUniqueTerms[1] + ";" + DocLength + "\n");
                            DocID.incrementAndGet();
                            IntervalsFilesData.append(docData.toString());
                        }
                    }
                }
                WriteToDocumentIDLexicon(docLexiconData);
                synchronized (TempPosingFileName) {
                    Write(IntervalsFilesData, TempPosingFileName.get());
                    TempPosingFileName.incrementAndGet();
                }
            });
        }
        this.threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!threadPoolExecutor.isTerminated()) ;
        isActive = false;
    }


    //-----------Setters & Getters----------------
    //--For term, doc, file ID's
    public void setAtomicIntegers(int termId, int docId, int fileId, int tempPosingFileName) {
        this.TermID.set(termId);
        this.DocID.set(docId);
        this.FileID.set(fileId);
        this.TempPosingFileName.set(tempPosingFileName);
    }

    public AtomicInteger getTermID() {
        return TermID;
    }

    public AtomicInteger getDocID() {
        return DocID;
    }

    public AtomicInteger getFileID() {
        return FileID;
    }

    public AtomicInteger getTempPosingFileName() {
        return TempPosingFileName;
    }

    //--For isActive

    public boolean isActive() {
        return isActive;
    }

    //--For Intervals: how much files to read for readFile class


    public void setIntervals(int intervals) {
        Intervals = intervals;
    }


    /**
     * Updating entities info:
     * 1. if term is already in dictionary them we update df, writing the term to the posting file
     * 2. if term is'nt in the dictionary, we check if the term is exist in the entities dictionary
     * 2.1. if the term is exist in the entities dictionary then we check:
     * 2.1.1 if df > 1 we are just writing the term info into the posting file
     * 2.1.2 if df = 1 we first writing the term with the df = 1 to posting file and after it the new term info and updating df
     * 2.2 we insert the new term to entities dictionary with df = 1
     *
     * @param alltokens
     * @param maxTFandUniqueTerms
     * @param fileData
     * @return true if its a new term
     */
    private void UpdateEntitiesInfo(ArrayList<Token>[] alltokens, Integer[] maxTFandUniqueTerms, StringBuilder fileData) {
        ArrayList<Token> tokens = alltokens[1];
        for (Token token : tokens) {
            if (token != null) {
                CountAndRemove(tokens, token, null);
                Token inDictionary = new Token(token);
                inDictionary.setName(Character.toLowerCase(token.getName().charAt(0)) + token.getName().substring(1));
                if (!Dictionary.containsKey(inDictionary))//if 'KINGDOM' -> 'kINGDOM' then we change all to 'kingdom'
                    inDictionary.setName(token.getName().toLowerCase());
                if (alltokens[0].contains(inDictionary)) {
                    Token forMaxTF = alltokens[0].get(alltokens[0].indexOf(inDictionary));
                    if (maxTFandUniqueTerms[0] < token.getTf() + forMaxTF.getTf())
                        maxTFandUniqueTerms[0] = token.getTf() + forMaxTF.getTf();
                }
                if (Dictionary.containsKey(inDictionary)) {//word is already exist in the dictionary
                    MutablePair<Integer, Integer> dfAndTermID = Dictionary.get(inDictionary);
                    dfAndTermID.setLeft(dfAndTermID.getLeft() + 1);
                    fileData.append(dfAndTermID.getRight() + ";" + DocID.get() + ";" + token.getTf() + ";" + token.isEntity() + ";" + token.getPositions() + "\n");
                } else {
                    if (EntitiesDictionary.containsKey(token)) {
                        ArrayList<Integer> dfTermIDandDocID = EntitiesDictionary.get(token);
                        if (dfTermIDandDocID.get(0) == 1) {//df is 1 so we will write it to posting file
                            Token firstOccurrence = getEntity(token);
                            fileData.append(dfTermIDandDocID.get(1) + ";" + dfTermIDandDocID.get(2) + ";" + firstOccurrence.getTf() + ";" + firstOccurrence.isEntity() + ";" + firstOccurrence.getPositions() + "\n");
                        }
                        fileData.append(dfTermIDandDocID.get(1) + ";" + DocID.get() + ";" + token.getTf() + ";" + token.isEntity() + ";" + token.getPositions() + "\n");
                        dfTermIDandDocID.set(0, dfTermIDandDocID.get(0) + 1);
                    } else {
                        ArrayList<Integer> arr = new ArrayList<>(3);
                        arr.add(0, 1);
                        arr.add(1, TermID.get());
                        arr.add(2, DocID.get());
                        EntitiesDictionary.put(token, arr);
                        maxTFandUniqueTerms[1]++;
                        TermID.incrementAndGet();
                    }
                }
            }
        }
    }


    /**
     * Creating new Term who will be inserted to the dictionary and will be written to a new posting file
     *
     * @param tokens
     * @param term
     * @param isNew               - if its a new term to be inserted to the dictionary
     * @param fileData
     * @param maxTFandUniqueTerms
     */
    private boolean UpdateTermInfo(ArrayList<Token> tokens, Token term, boolean isNew, StringBuilder fileData, Integer[] maxTFandUniqueTerms) {
        CountAndRemove(tokens, term, maxTFandUniqueTerms);
        //checking if term is already exist in entities dictionary//
        boolean entityExist = false;
        if (isNew && !term.getName().isEmpty() && ((term.getName().charAt(0) >= 'a' && term.getName().charAt(0) <= 'z')
                || (term.getName().charAt(0) >= 'A' && term.getName().charAt(0) <= 'Z'))) {
            Token token;
            Token allUpper = new Token(term);
            allUpper.setName(allUpper.getName().toUpperCase());
            if (EntitiesDictionary.containsKey(allUpper))
                token = allUpper;
            else {
                char upper = Character.toUpperCase(term.getName().charAt(0));
                token = new Token(term);
                token.setName(upper + term.getName().substring(1));
            }
            if (EntitiesDictionary.containsKey(token) && !term.isEmpty() && !token.isEmpty()) {
                entityExist = true;
                ArrayList<Integer> dfTermIDandDocID = EntitiesDictionary.get(token);
                if (dfTermIDandDocID.get(0) > 1) {//we met only this term with upper char until now, df > 1
                    fileData.append(dfTermIDandDocID.get(1) + ";" + DocID.get() + ";" + term.getTf() + ";" + term.isEntity() + ";" + term.getPositions() + "\n");
                    dfTermIDandDocID.set(0, dfTermIDandDocID.get(0) + 1);
                    term.setEntity(false);
                    Dictionary.put(term, new MutablePair<>(dfTermIDandDocID.get(0), dfTermIDandDocID.get(1)));
                }
                //df = 1 --> concat to posting file the upper term, writing the lower term too and updating df in the dictionary
                else {
                    token = getEntity(token);
                    token.setEntity(false);
                    if (dfTermIDandDocID.get(2) != DocID.get()) {
                        fileData.append(dfTermIDandDocID.get(1) + ";" + dfTermIDandDocID.get(2) + ";" + token.getTf() + ";" + token.isEntity() + ";" + token.getPositions() + "\n");
                        fileData.append(dfTermIDandDocID.get(1) + ";" + DocID.get() + ";" + term.getTf() + ";" + term.isEntity() + ";" + term.getPositions() + "\n");
                    } else {
                        term.setTf(term.getTf() + 1);
                        term.addPosition(token.getPosition());
                        fileData.append(dfTermIDandDocID.get(1) + ";" + DocID.get() + ";" + term.getTf() + ";" + term.isEntity() + ";" + term.getPositions() + "\n");
                    }
                    Dictionary.put(term, new MutablePair<>(2, dfTermIDandDocID.get(1)));
                }
                if (maxTFandUniqueTerms[0] < term.getTf() + token.getTf() && DocID.get() == dfTermIDandDocID.get(2))
                    maxTFandUniqueTerms[0] = term.getTf() + token.getTf();
                EntitiesDictionary.entrySet().remove(token);
            }
        }
        if (!entityExist) {
            if (isNew) {
                fileData.append(TermID.get() + ";" + DocID.get() + ";" + term.getTf() + ";" + term.isEntity() + ";" + term.getPositions() + "\n");
                Dictionary.put(term, new MutablePair<>(1, TermID.get()));
                if (maxTFandUniqueTerms[0] < term.getTf())
                    maxTFandUniqueTerms[0] = term.getTf();
                return true;
            } else {
                MutablePair<Integer, Integer> dfANDtermID = Dictionary.get(term);
                int currentDF = dfANDtermID.getLeft() + 1;
                dfANDtermID.setLeft(currentDF);
                fileData.append(dfANDtermID.getRight() + ";" + DocID.get() + ";" + term.getTf() + ";" + term.isEntity() + ";" + term.getPositions() + "\n");
            }
        }
        return false;
    }

    private Token getEntity(Token token) {
        for (Token firstOccurrence : EntitiesDictionary.keySet()) {
            if (token.equals(firstOccurrence))
                return firstOccurrence;
        }
        return null;
    }

    /**
     * This function updating tf and positions for a term according to the given tokens list and removes the rest duplications
     *
     * @param tokens
     * @param term
     * @param maxTFandUniqueTerms
     */
    private void CountAndRemove(ArrayList<Token> tokens, Token term, Integer[] maxTFandUniqueTerms) {
        ArrayList<Integer> toDelete = new ArrayList<>();
        int i = 0;
        for (Token token : tokens) {
            if (token != null && !term.isEqual(token)) {
                if (term.getName().equals(token.getName())) {
                    term.increaseTF();
                    if (maxTFandUniqueTerms != null && maxTFandUniqueTerms[0] < term.getTf())
                        maxTFandUniqueTerms[0] = term.getTf();
                    term.addPosition(token.getPosition());
                    toDelete.add(i);
                } else if (term == null || term.getName().length() == 0)
                    toDelete.add(i);
            }
            i++;
        }
        for (Integer delete : toDelete) {
            tokens.set(delete.intValue(), null);
        }
        toDelete.clear();
        term.sortPositionsByGaps();
        if (maxTFandUniqueTerms != null)
            maxTFandUniqueTerms[1]++;
    }


    /**
     * Seperating the TEXT from the rest of the documents labels
     *
     * @param doc
     * @return only TEXT with the labels <TEXT>
     */
    private String ExtractText(String doc) {
        int start = doc.indexOf("<TEXT>");
        int end = doc.indexOf("</TEXT>") + 7;
        if (start < 0 || end < 0)
            return "";
        StringBuilder text = new StringBuilder(doc.substring(start, end));
        return text.toString();
    }

    /**
     * Writing to disk the File Lexicon, it will assign FileNO to a FileID
     *
     * @param fileNo
     */
    private synchronized void WriteToFileIDLexicon(String fileNo) {
        File file = new File(SavingPostingFilePath + "\\FileIDLexicon.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(fileNo + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * //     * @param docName
     * //     * @param fileID
     * //     * @param maxTF
     * //     * @param uniqueWords
     */
    private synchronized void WriteToDocumentIDLexicon(StringBuilder SB) {
        File file = new File(SavingPostingFilePath + "\\DocIDLexicon.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(SB.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Merging terms and upper lower case
     */
    public void mergeTerms(StringBuilder sb) {
        ArrayList<String[]> data = new ArrayList<>();
        String[] string = sb.toString().split("\n");
        for (String s : string) {
            if (s != null && !s.isEmpty()) {
                int termid = Integer.valueOf(s.indexOf(";"));
                int docid = Integer.valueOf(s.indexOf(";", termid + 1));
                int tf = Integer.valueOf(s.indexOf(";", docid + 1));
                int isEntity = Integer.valueOf(s.indexOf(";", tf + 1));
                String[] term = new String[5];
                term[0] = s.substring(0, termid);
                term[1] = s.substring(termid + 1, docid);
                term[2] = s.substring(docid + 1, tf);
                term[3] = s.substring(tf + 1, isEntity);
                term[4] = s.substring(isEntity + 1);//positions
                data.add(term);
            }
        }


        int capacity = data.size();
        for (int i = 0; i < capacity; ) {
            String[] currentTerm = data.get(i);
            int j = i + 1;
            while (j < capacity) {
                boolean capacityChanged = false;
                if (data.get(j)[0].equals(currentTerm[0]) && data.get(j)[1].equals(currentTerm[1])) {
                    data.get(i)[2] = String.valueOf(Integer.valueOf(data.get(j)[2]) + Integer.valueOf(data.get(i)[2]));
                    String[] TermPosition = data.get(i)[4].split(",");
                    StringBuilder termPositions = new StringBuilder();
                    String[] arrPositions = data.get(j)[4].split(",");
                    if (arrPositions.length > 0 && !arrPositions[0].isEmpty()) {
                        int k = 0;
                        int arrPos = Integer.parseInt(arrPositions[0]);
                        for (int t = 0; t < TermPosition.length && !TermPosition[t].isEmpty(); ) {
                            int curPosition = Integer.valueOf(TermPosition[t]);
                            while (k < arrPositions.length && curPosition > arrPos) {
                                termPositions.append(arrPos + ",");
                                curPosition -= arrPos;
                                k++;
                                if (k < arrPositions.length)
                                    arrPos = Integer.parseInt(arrPositions[k]);
                            }
                            termPositions.append(curPosition + ",");
                            arrPos -= curPosition;
                            t++;
                        }
                        if (arrPos > 0)
                            termPositions.append(arrPos);
                        else
                            termPositions.replace(termPositions.length()-1,termPositions.length(),"");
                    }
                    data.get(i)[4] = termPositions.toString();
                    capacity--;
                    capacityChanged = true;
                    data.remove(j);
                }
                if (!capacityChanged)
                    j++;
            }
            i++;
        }
        data.sort(myComparator);
        sb.setLength(0);
        for (String[] term : data) {
            sb.append(term[0] + ";" + term[1] + ";" + term[2] + ";" + term[3] + ";" + term[4] + "\n");
        }

    }

    /**
     * @param data     String builder which will be written to a posting file
     * @param fileName of the written posting file
     */
    private void Write(StringBuilder data, int fileName) {
        if (data != null && data.length() != 0) {
            File file = new File(SavingPostingFilePath + "\\" + fileName + ".txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileWriter fileWriter = new FileWriter(file, true);
                fileWriter.write(data.toString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
