import Rules.Token;
import javafx.print.Collation;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Indexer {

    private String CorpusPath;
    private String SavingPostingFilePath;
    private boolean isStemmer;
    private ThreadPoolExecutor threadPoolExecutor;
    private HashMap<Token, Pair<Integer,LinkedList<Integer>>> Dictionary;
    private HashMap<Token, Pair<Integer,LinkedList<Integer>>> UpperLowerDictionary;
    private HashMap<Token, Pair<Integer,LinkedList<Integer>>> EntitiesDictionary;

    public Indexer(String corpusPath, String savingPostingFilePath, boolean stemmer) {
        CorpusPath = corpusPath;
        SavingPostingFilePath = savingPostingFilePath;
        isStemmer = stemmer;
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    }
}
