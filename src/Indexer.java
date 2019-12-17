import Rules.Token;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class Indexer {

    private String CorpusPath;
    private String SavingPostingFilePath;
    private boolean isStemmer;
    private ThreadPoolExecutor threadPoolExecutor;
    private ConcurrentHashMap<Token, MutablePair<Integer, Integer>> Dictionary;//term-#doc-termID
    //    private HashMap<Token, MutablePair<Integer, Integer>> EntitiesDictionary;
    private ConcurrentHashMap<Token, ArrayList<Integer>> EntitiesDictionary;
    //    private HashMap<String,Integer> DocIDLexicon;
//    private HashMap<String, Integer> FileIDLexicon;// we will write them directly to a file that map them
    private MaxentTagger maxentTagger;
    private AtomicInteger TermID;
    private AtomicInteger DocID;
    private AtomicInteger FileID;

    public Indexer(String corpusPath, String savingPostingFilePath, boolean stemmer) {
        CorpusPath = corpusPath;
        SavingPostingFilePath = savingPostingFilePath;
        isStemmer = stemmer;
        Dictionary = new ConcurrentHashMap<>();
        EntitiesDictionary = new ConcurrentHashMap<>();
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        String modelFile = "Resources/english-left3words-distsim.tagger";
        maxentTagger = new MaxentTagger(modelFile, StringUtils.argsToProperties(new String[]{"-model", modelFile}), false);
        TermID = new AtomicInteger(0);
        DocID = new AtomicInteger(0);
        FileID = new AtomicInteger(0);
    }

    public void Index() {
        File[] files = (new File(CorpusPath)).listFiles();
        for (File file : files) {
            File[] currentDirectory = file.listFiles();
            ReadFile fileReader = new ReadFile(currentDirectory[0].getAbsolutePath());
            threadPoolExecutor.execute(() -> {
                StringBuilder fileData = new StringBuilder();
                while (!fileReader.isEmpty()) {
                    String Doc = fileReader.getNextDoc();
                    String Text = ExtractText(Doc);
                    if (Text.length() > 0) {
                        Parser parser = new Parser(Text, isStemmer);
                        ArrayList<Token>[] tokenList = parser.Parse(maxentTagger);
                        int[] maxTFandUniqueTerms = new int[2];
                        maxTFandUniqueTerms[0] = 0;
                        maxTFandUniqueTerms[1] = 0;
                        String DocNo = fileReader.getDocNO();
                        for (Token term : tokenList[0]) {
//                        mutex[1].lock();//locking if 1 thread is updating entities files before thread2 updating term info
                            if (term != null) {
                                if (!Dictionary.containsKey(term)) {
                                    UpdateTermInfo(tokenList[0], term, true, fileData);//including mapping termID
                                    TermID.incrementAndGet();
                                    if (term.getTf() > maxTFandUniqueTerms[0])
                                        maxTFandUniqueTerms[0] = term.getTf();
                                    maxTFandUniqueTerms[1]++;
                                } else
                                    UpdateTermInfo(tokenList[0], term, false, fileData);
                            }
                        }
                        if (UpdateEntitiesInfo(tokenList[1], maxTFandUniqueTerms, fileData))
                            TermID.incrementAndGet();
                        WriteToDocumentIDLexicon(DocNo, FileID.get(), maxTFandUniqueTerms[0], maxTFandUniqueTerms[1]);
                        DocID.incrementAndGet();
                    }
                }
                WriteToFileIDLexicon(fileReader.getFileNO());
                WritePostingFile(fileData, FileID.get());
                FileID.incrementAndGet();
            });
        }
        this.threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //sort dictionary before writing it to the disk after merging dictionary and entities//
//        for (Token entity : EntitiesDictionary.keySet()) {
//            if (entity != null && EntitiesDictionary.get(entity) != null
//                    && EntitiesDictionary.get(entity).get(0) > 1 && EntitiesDictionary.get(entity).get(1) != null) {
//                entity.setName(entity.getName().toUpperCase());
//                Dictionary.put(entity, new MutablePair<>(EntitiesDictionary.get(entity).get(0), EntitiesDictionary.get(entity).get(1)));
//            }
//        }
        Comparator<Token> comparator = new Comparator<Token>() {
            @Override
            public int compare(Token o1, Token o2) {
                if (o1.getName().compareTo(o2.getName()) > 0)
                    return 1;
                else
                    return -1;
            }
        };

//        SortedSet<Token> sortedDictionary = new TreeSet<>(comparator);
//        sortedDictionary.addAll(Dictionary.keySet());
//        WriteDictionaryToFile();

    }

    /**
     * Writing the sorted dictionary into a file
     */
    private void WriteDictionaryToFile() {
        File file = new File(SavingPostingFilePath + "\\Dictionary.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            for (Token tuple : Dictionary.keySet()) {
                StringBuilder data = new StringBuilder();
                MutablePair<Integer, Integer> dfAndTermID = Dictionary.get(tuple);
                data.append(tuple + ";" + dfAndTermID.getLeft() + ";" + dfAndTermID.getRight());
                fileWriter.write(data.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
     * @param tokens
     * @param maxTFandUniqueTerms
     * @param fileData
     * @return true if its a new term
     */
    private synchronized boolean UpdateEntitiesInfo(ArrayList<Token> tokens, int[] maxTFandUniqueTerms, StringBuilder fileData) {
        for (Token token : tokens) {
            if (token != null) {
                CountAndRemove(tokens, token);
                Token inDictionary = new Token(token);
                inDictionary.setName(Character.toLowerCase(token.getName().charAt(0)) + token.getName().substring(1));
                if (maxTFandUniqueTerms[0] < token.getTf())
                    maxTFandUniqueTerms[0] = token.getTf();
                if (Dictionary.containsKey(inDictionary)) {//word is already exist in the dictionary
                    MutablePair<Integer, Integer> dfAndTermID = Dictionary.get(inDictionary);
                    dfAndTermID.setLeft(dfAndTermID.getLeft() + 1);
                    fileData.append(dfAndTermID.getRight() + ";" + DocID.get() + ";" + token.getTf() + ";" + token.getPositions() + "\n");
//                    WriteToPostingFile(dfAndTermID.getRight(), DocID.get(), token.getTf(), token.getPositions());
                } else {
                    if (EntitiesDictionary.containsKey(token)) {
                        ArrayList<Integer> dfTermIDandDocID = EntitiesDictionary.get(token);
                        if (dfTermIDandDocID.get(0) == 1) {//df is 1 so we will write it to posting file
                            Token firstOccurrence = getEntity(token);
                            fileData.append(dfTermIDandDocID.get(1) + ";" + dfTermIDandDocID.get(2) + ";" + firstOccurrence.getTf() + ";" + firstOccurrence.getPositions() + "\n");
//                            WriteToPostingFile(dfTermIDandDocID.get(1), dfTermIDandDocID.get(2), firstOccurrence.getTf(), firstOccurrence.getPositions());
                        }
                        fileData.append(dfTermIDandDocID.get(1) + ";" + dfTermIDandDocID.get(2) + ";" + token.getTf() + ";" + token.getPositions() + "\n");
//                        WriteToPostingFile(dfTermIDandDocID.get(1), DocID.get(), token.getTf(), token.getPositions());
                        dfTermIDandDocID.set(0, dfTermIDandDocID.get(0) + 1);
                    } else {
                        ArrayList<Integer> arr = new ArrayList<>(3);
                        arr.add(0, 1);
                        arr.add(1, TermID.get());
                        arr.add(2, DocID.get());
                        EntitiesDictionary.put(token, arr);
                        maxTFandUniqueTerms[1]++;
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Creating new Term who will be inserted to the dictionary and will be written to a new posting file
     *
     * @param tokens
     * @param term
     * @param isNew    - if its a new term to be inserted to the dictionary
     * @param fileData
     */
    private synchronized void UpdateTermInfo(ArrayList<Token> tokens, Token term, boolean isNew, StringBuilder fileData) {
        CountAndRemove(tokens, term);
        //checking if term is already exist in entities dictionary//
        boolean entityExist = false;
        if (isNew && !term.getName().isEmpty() && ((term.getName().charAt(0) >= 'a' && term.getName().charAt(0) <= 'z')
                || (term.getName().charAt(0) >= 'A' && term.getName().charAt(0) <= 'Z'))) {
            char upper = Character.toUpperCase(term.getName().charAt(0));
            Token token = new Token(term);
            token.setName(upper + term.getName().substring(1));
            if (EntitiesDictionary.containsKey(token)) {
                entityExist = true;
                ArrayList<Integer> dfTermIDandDocID = EntitiesDictionary.get(token);
                if (dfTermIDandDocID.get(0) > 1) {//we met only this term with upper char until now, df > 1
                    fileData.append(dfTermIDandDocID.get(1) + ";" + DocID.get() + ";" + term.getTf() + ";" + term.getPositions() + "\n");
//                    WriteToPostingFile(dfTermIDandDocID.get(1), DocID.get(), term.getTf(), term.getPositions());
                    dfTermIDandDocID.set(0, dfTermIDandDocID.get(0) + 1);
                    Dictionary.put(term, new MutablePair<>(dfTermIDandDocID.get(0), dfTermIDandDocID.get(1)));
                }
                //df = 1 --> concat to posting file the upper term, writing the lower term too and updating df in the dictionary
                else {
                    token = getEntity(token);
                    fileData.append(dfTermIDandDocID.get(1) + ";" + dfTermIDandDocID.get(2) + ";" + token.getTf() + ";" + token.getPositions() + "\n");
                    fileData.append(dfTermIDandDocID.get(1) + ";" + DocID.get() + ";" + term.getTf() + ";" + term.getPositions() + "\n");
//                    WriteToPostingFile(dfTermIDandDocID.get(1), dfTermIDandDocID.get(2), token.getTf(), token.getPositions());
//                    WriteToPostingFile(dfTermIDandDocID.get(1), DocID.get(), term.getTf(), term.getPositions());
                    Dictionary.put(term, new MutablePair<>(2, dfTermIDandDocID.get(1)));
                }
                EntitiesDictionary.entrySet().remove(token);
            }
        }
        if (!entityExist) {
            if (isNew) {
                fileData.append(TermID.get() + ";" + DocID.get() + ";" + term.getTf() + ";" + term.getPositions() + "\n");
//                WriteToPostingFile(TermID.get(), DocID.get(), term.getTf(), term.getPositions());
                Dictionary.put(term, new MutablePair<>(1, TermID.get()));
            } else {
                MutablePair<Integer, Integer> dfANDtermID = Dictionary.get(term);
                int currentDF = dfANDtermID.getLeft() + 1;
                dfANDtermID.setLeft(currentDF);
                fileData.append(dfANDtermID.getRight() + ";" + DocID.get() + ";" + term.getTf() + ";" + term.getPositions() + "\n");
//                WriteToPostingFile(dfANDtermID.getRight(), DocID.get(), term.getTf(), term.getPositions());
            }
        }
    }

    private synchronized Token getEntity(Token token) {
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
     */
    private void CountAndRemove(ArrayList<Token> tokens, Token term) {
        LinkedList<Integer> toDelete = new LinkedList<>();
        int i = 0;
        for (Token token : tokens) {
            if (token != null && !term.isEqual(token)) {
                if (term.getName().equals(token.getName())) {
                    term.increaseTF();
                    term.addPosition(token.getPosition());
                    toDelete.add(i);
                }
            }
            i++;
        }
        for (Integer delete : toDelete) {
            tokens.set(delete.intValue(), null);
        }
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
            fileWriter.write(fileNo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param docName
     * @param fileID
     * @param maxTF
     * @param uniqueWords
     */
    private synchronized void WriteToDocumentIDLexicon(String docName, int fileID, int maxTF, int uniqueWords) {
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
            fileWriter.write(docName + ";" + fileID + ";" + maxTF + ";" + uniqueWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WritePostingFile(StringBuilder data, int fileID) {
        File file = new File(SavingPostingFilePath + "\\" + fileID + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param termID
     * @param docID
     * @param tf
     * @param positions
     */
    //writing
    private synchronized void WriteToPostingFile(int termID, int docID, int tf, String positions) {
        File file = new File(SavingPostingFilePath + "\\" + termID + ".txt");
        try {
            boolean termExistinSameDocID = false;
            StringBuilder termInfo = new StringBuilder();
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferReader = new BufferedReader(fileReader);
                String str;
                while ((str = bufferReader.readLine()) != null) {
                    String[] docIDTfPositions = str.split(";");
                    if (docID == Integer.parseInt(docIDTfPositions[0]) && docIDTfPositions.length > 2) {
                        docIDTfPositions[1] = String.valueOf(Integer.parseInt(docIDTfPositions[1]) + tf);
                        String[] TermPosition = docIDTfPositions[2].split(",");
                        StringBuilder termPositions = new StringBuilder();
                        String[] arrPositions = positions.split(",");
                        if (arrPositions.length > 0 && !arrPositions[0].isEmpty()) {
                            termExistinSameDocID = true;
                            int i = 0;
                            int arrPos = Integer.parseInt(arrPositions[0]);
                            for (int j = 0; j < TermPosition.length; j++) {
                                int curPosition = Integer.valueOf(TermPosition[j]);
                                while (i < arrPositions.length && curPosition > arrPos) {
                                    termPositions.append(arrPos + ",");
                                    curPosition -= arrPos;
                                    i++;
                                    if (i < arrPositions.length)
                                        arrPos = Integer.parseInt(arrPositions[i]);
                                }
                                termPositions.append(curPosition + ",");
                                arrPos -= curPosition;
                            }
                            str = docIDTfPositions[0] + ";" + docIDTfPositions[1] + ";" + termPositions.toString();
                        }
                    }
                    termInfo.append(str + "\n");
                    //termInfo.append(System.lineSeparator());
                }
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileWriter fileWriter;
            if (termExistinSameDocID) {
                fileWriter = new FileWriter(file, false);
                fileWriter.write(termInfo.toString());
            } else
                fileWriter = new FileWriter(file, true);
            fileWriter.write(docID + ";" + tf + ";" + positions + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    /**
//     *
//     * @param list
//     * @param term
//     * @return calculated tf of a term in a document
//     */
//    private int CreateTf(LinkedList<Token> list, Token term) {
//        int counter = 0;
//        for (Token token: list) {
//            if (token.getName().equals(term.getName()))
//                counter++;
//        }
//        return counter;
//    }
}
