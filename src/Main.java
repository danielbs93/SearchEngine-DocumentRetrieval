import Rules.Token;
import org.apache.commons.lang3.tuple.MutablePair;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
//    private ConcurrentHashMap<Token, MutablePair<Integer, Integer>> Dictionary;//term-#doc-termID
//    private ConcurrentHashMap<Token, ArrayList<Integer>> EntitiesDictionary;
//private
    public static void main(String[] args) {
        String CorpusPath = "C:\\Users\\erant\\Desktop\\project\\corpus";
        String CorpusPath2 = "C:\\Users\\erant\\Desktop\\corpus";
        String savingPath = "C:\\Users\\erant\\Desktop\\project\\postingFiles";
        String savingPath2 = "C:\\Users\\erant\\Desktop\\project\\postingFiles2";
        AtomicInteger DocID = new AtomicInteger(0);
        AtomicInteger TermID = new AtomicInteger(0);
        AtomicInteger FileID = new AtomicInteger(0);
        ConcurrentHashMap<Token, MutablePair<Integer, Integer>> Dictionary = new ConcurrentHashMap<>();
        ConcurrentHashMap<Token, ArrayList<Integer>> EntitiesDictionary = new ConcurrentHashMap<>();
        Indexer indexer = new Indexer(Dictionary,EntitiesDictionary,CorpusPath,savingPath,true,DocID,TermID,FileID);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        indexer.Index();
        Indexer indexer2 = new Indexer(Dictionary,EntitiesDictionary,CorpusPath2,savingPath2,true,DocID,TermID,FileID);
        indexer2.Index();
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp2.getTime()-timestamp.getTime());
    }
}
