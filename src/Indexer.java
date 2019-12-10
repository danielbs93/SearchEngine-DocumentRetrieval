import Rules.Token;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
import javafx.util.Pair;
import sun.awt.Mutex;

import java.io.*;
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
    private HashMap<Token, Pair<Integer,Integer>> Dictionary;//term-#doc-termID
    private HashMap<Token, Pair<Integer,Integer>> EntitiesDictionary;
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
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
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
                    LinkedList<Token>[] tokenList = parser.Parse(maxentTagger);
                    Mutex[] mutex = new Mutex[3];
                    for (Mutex m: mutex) {
                        m = new Mutex();
                    }
                    int[] maxTFandUniqueTerms = new int[2];
                    maxTFandUniqueTerms[0] = 0;
                    maxTFandUniqueTerms[1] = 0;
                    for (Token term: tokenList[0]) {
                        mutex[0].lock();
                        if (!Dictionary.containsKey(term)) {
                            maxTFandUniqueTerms[1] += UpdateTermInfo(tokenList[0], term);//including mapping termID
                            if (term.getTf() > maxTFandUniqueTerms[0])
                                maxTFandUniqueTerms[0] = term.getTf();
                        }
                        mutex[0].unlock();
                    }
                    mutex[1].lock();
                    maxTFandUniqueTerms = UpdateEntitiesInfo(tokenList[1],maxTFandUniqueTerms);
                    mutex[1].unlock();
                    String DocNo = fileReader.getDocNO();
                    String FileNo = fileReader.getFileNO();

                    mutex[2].lock();
                    WriteToFileIDLexicon(FileNo);
                    WriteToDocumentIDLexicon(DocNo,FileID.get(),maxTFandUniqueTerms[0],maxTFandUniqueTerms[1]);
                    mutex[2].unlock();
                    DocID.incrementAndGet();
                }
            });
            FileID.incrementAndGet();

        }
    }

    private int UpdateTermInfo(LinkedList<Token> tokens, Token term) {
        int uniqueTerm = 0;
        int i = 0;
        for (Token token:tokens) {
            if (!term.equals(token)) {
                if (term.getName().equals(token.getName())) {
                    term.increaseTF();
                    term.addPosition(token.getPosition());
                    uniqueTerm++;
                    tokens.remove(i);
                }
            }
            i++;
        }
        Dictionary.put(term, new Pair<>(1,TermID.getAndIncrement()));
        return uniqueTerm;
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
            fileWriter.write(docID+";"+tf+";"+positions+ ">\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mutex.unlock();
    }

    /**
     *
     * @param list
     * @param term
     * @return calculated tf of a term in a document
     */
    private int CreateTf(LinkedList<Token> list, Token term) {
        int counter = 0;
        for (Token token: list) {
            if (token.getName().equals(term.getName()))
                counter++;
        }
        return counter;
    }
}
