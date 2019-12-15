import Rules.Token;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import sun.awt.Mutex;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class Indexer {

    private String CorpusPath;
    private String SavingPostingFilePath;
    private boolean isStemmer;
    private ThreadPoolExecutor threadPoolExecutor;
    private HashMap<Token, MutablePair<Integer,Integer>> Dictionary;//term-#doc-termID
    private HashMap<Token, MutablePair<Integer,Integer>> EntitiesDictionary;
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
        Dictionary = new HashMap<>();
        EntitiesDictionary = new HashMap<>();
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        String modelFile = "Resources/english-left3words-distsim.tagger";
        maxentTagger = new MaxentTagger(modelFile, StringUtils.argsToProperties(new String[]{"-model", modelFile}),false);
        TermID = new AtomicInteger(0);
        DocID = new AtomicInteger(0);
        FileID = new AtomicInteger(0);
    }

    public void Index() {
        File[] files = (new File(CorpusPath)).listFiles();
        for (File file: files) {
            File[] currentDirectory = file.listFiles();
            ReadFile fileReader = new ReadFile(currentDirectory[0].getAbsolutePath());
            threadPoolExecutor.execute(() -> {
                while (!fileReader.isEmpty()) {
                    String Doc = fileReader.getNextDoc();
                    String Text = ExtractText(Doc);
                    Parser parser = new Parser(Text,isStemmer);
                    ArrayList<Token>[] tokenList = parser.Parse(maxentTagger);
                    Mutex[] mutex = new Mutex[2];
                    mutex[0] = new Mutex();
                    mutex[1] = new Mutex();
                    int[] maxTFandUniqueTerms = new int[2];
                    maxTFandUniqueTerms[0] = 0;
                    maxTFandUniqueTerms[1] = 0;
                    mutex[0].lock();
                    for (Token term: tokenList[0]) {
//                        mutex[1].lock();//locking if 1 thread is updating entities files before thread2 updating term info
                        if (!Dictionary.containsKey(term)) {
                            UpdateTermInfo(tokenList[0], term,true);//including mapping termID
                            if (term.getTf() > maxTFandUniqueTerms[0])
                                maxTFandUniqueTerms[0] = term.getTf();
                        }else
                            UpdateTermInfo(tokenList[0],term,false);
                        maxTFandUniqueTerms[1]++;
//                        mutex[0].unlock();
                    }
                    UpdateEntitiesInfo(tokenList[1],maxTFandUniqueTerms);
                    mutex[0].unlock();
                    mutex[1].lock();
                    String DocNo = fileReader.getDocNO();
                    WriteToDocumentIDLexicon(DocNo,FileID.get(),maxTFandUniqueTerms[0],maxTFandUniqueTerms[1]);
                    mutex[1].unlock();
                    DocID.incrementAndGet();
                }
            });
            WriteToFileIDLexicon(fileReader.getFileNO());
            FileID.incrementAndGet();
        }
        this.threadPoolExecutor.shutdown();
        //sort dictionary before writing it to the disk//
    }

    /**
     * Updating entities info:
     * 1. if term is already in dictionary them we update df, writing the term to the posting file
     * 2. if term is'nt in the dictionary, we check if the term is exist in the entities dictionary
     * 2.1. if the term is exist in the entities dictionary then we check:
     * 2.1.1 if df > 1 we are just writing the term info into the posting file
     * 2.1.2 if df = 1 we first writing the term with the df = 1 to posting file and after it the new term info and updating df
     * 2.2 we insert the new term to entities dictionary with df = 1
     * @param tokens
     * @param maxTFandUniqueTerms
     */
    private void UpdateEntitiesInfo(ArrayList<Token> tokens, int[] maxTFandUniqueTerms) {
        for (Token token: tokens) {
            CountAndRemove(tokens,token);
            Token inDictionary = new Token(token);
            inDictionary.setName(Character.toLowerCase(token.getName().charAt(0)) + token.getName().substring(1));
            if (Dictionary.containsKey(inDictionary)) {
                MutablePair<Integer,Integer> dfAndTermID = Dictionary.get(inDictionary);
                dfAndTermID.setLeft(dfAndTermID.getLeft() + 1);
                WriteToPostingFile(dfAndTermID.getRight(),DocID.get(),token.getTf(),token.getPositions());
            }
            else {
                if (EntitiesDictionary.containsKey(token)) {
                    MutablePair<Integer,Integer> dfAndTermID = EntitiesDictionary.get(token);
                    if (dfAndTermID.getLeft() == 1) {
                        Token firstOccurrence = new Token();
                        for (Token inEntitiesDictionary : EntitiesDictionary.keySet()) {
                            if (inEntitiesDictionary.isEqual(token))
                                firstOccurrence = inEntitiesDictionary;
                        }
                        WriteToPostingFile(dfAndTermID.getRight(),DocID.get(),firstOccurrence.getTf(),firstOccurrence.getPositions());
                    }
                    WriteToPostingFile(dfAndTermID.getRight(),DocID.get(),token.getTf(),token.getPositions());
                    dfAndTermID.setLeft(dfAndTermID.getLeft() + 1);
                }
                else {
                    EntitiesDictionary.put(token, new MutablePair<>(1, TermID.get()));
                    TermID.incrementAndGet();
                }
            }
        }
    }


    /**
     * Creating new Term who will be inserted to the dictionary and will be written to a new posting file
     * @param tokens
     * @param term
     * @param isNew - if its a new term to be inserted to the dictionary
     *
     */
    private void UpdateTermInfo(ArrayList<Token> tokens, Token term, boolean isNew) {
        CountAndRemove(tokens,term);
        //checking if term is already exist in entities dictionary//
        boolean entityExist = false;
        if (isNew && !term.getName().isEmpty() && ((term.getName().charAt(0) >= 'a' && term.getName().charAt(0) <= 'z')
                    || (term.getName().charAt(0) >= 'A' && term.getName().charAt(0) <='Z'))) {
            char upper = Character.toUpperCase(term.getName().charAt(0));
            Token token = new Token(term);
            token.setName(upper + term.getName().substring(1));
            if (EntitiesDictionary.containsKey(token)) {
                entityExist = true;
                MutablePair<Integer,Integer> dfAndTermID = EntitiesDictionary.get(token);
                if (dfAndTermID.getLeft() > 1) {//we met only this term with upper char until now
                    WriteToPostingFile(dfAndTermID.getRight(),DocID.get(),term.getTf(),term.getPositions());
                    dfAndTermID.setLeft(dfAndTermID.getLeft() + 1);
                    Dictionary.put(term,dfAndTermID);
                }
                //df = 1 --> concat to posting file the upper term, writing the lower term too and updating df in the dictionary
                else {
                    for (Token firstOccurrence: EntitiesDictionary.keySet()) {
                        if (token.equals(firstOccurrence))
                            token = firstOccurrence;
                    }
                    WriteToPostingFile(dfAndTermID.getRight(),DocID.get(),token.getTf(),token.getPositions());
                    WriteToPostingFile(dfAndTermID.getRight(),DocID.get(),term.getTf(),term.getPositions());
                    Dictionary.put(term,new MutablePair<>(2,dfAndTermID.getRight()));
                }
                EntitiesDictionary.entrySet().remove(token);
            }
        }
        if (!entityExist) {
            WriteToPostingFile(TermID.get(), DocID.get(), term.getTf(), term.getPositions());
            if (isNew) {
                Dictionary.put(term, new MutablePair<>(1, TermID.get()));
                TermID.incrementAndGet();
            } else {
                MutablePair<Integer, Integer> dfANDtermID = Dictionary.get(term);
                int currentDF = dfANDtermID.getLeft() + 1;
                dfANDtermID.setLeft(currentDF);
            }
        }
    }

    /**
     * This function updating tf and positions for a term according to the given tokens list and removes the rest duplications
     * @param tokens
     * @param term
     */
    private void CountAndRemove(ArrayList<Token> tokens, Token term) {
        LinkedList<Integer> toDelete = new LinkedList<>();
        int i = 0;
        for (Token token:tokens) {
            if (!term.isEqual(token)) {
                if (term.getName().equals(token.getName())) {
                    term.increaseTF();
                    term.addPosition(token.getPosition());
                    toDelete.add(i);
                }
            }
            i++;
        }
        for (Integer delete:toDelete) {
            tokens.set(delete.intValue(),null);
        }
    }

    /**
     * Seperating the TEXT from the rest of the documents labels
     * @param doc
     * @return only TEXT with the labels <TEXT>
     */
    private String ExtractText(String doc) {
        int start = doc.indexOf("<TEXT>");
        int end = doc.indexOf("</TEXT>") + 7;
        StringBuilder text = new StringBuilder(doc.substring(start,end));
        return text.toString();
    }

    /**
     * Writing to disk the File Lexicon, it will assign FileNO to a FileID
     * @param fileNo
     */
    private void WriteToFileIDLexicon(String fileNo) {
        File file = new File(SavingPostingFilePath + "\\FileIDLexicon.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(fileNo + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * @param docName
     * @param fileID
     * @param maxTF
     * @param uniqueWords
     */
    //lock mutex before using this method
    private void WriteToDocumentIDLexicon(String docName, int fileID, int maxTF, int uniqueWords) {
        File file = new File(SavingPostingFilePath + "\\DocIDLexicon.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(docName + ";" + fileID + ";" + maxTF + ";" + uniqueWords + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param termID
     * @param docID
     * @param tf
     * @param positions
     */
    //writing
    private void WriteToPostingFile(int termID, int docID, int tf, String positions){
        File file = new File(SavingPostingFilePath + "\\" + termID + ".txt");
        Mutex mutex = new Mutex();
        mutex.lock();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(docID+";"+tf+";"+positions+ "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mutex.unlock();
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
