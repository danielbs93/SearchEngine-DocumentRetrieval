import Rules.Token;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class Manager {
    private ConcurrentHashMap<Token, MutablePair<Integer, Integer>> Dictionary;//term-#doc-termID
    private ConcurrentHashMap<Token, ArrayList<Integer>> EntitiesDictionary;
    private boolean isStemmer;
    private String CorpusPath;
    private String SavingPath;
    private Indexer Indexer;
    private AtomicInteger TermID;
    private AtomicInteger DocID;
    private AtomicInteger FileID;
    private Comparator<String> myComparator;

    public Manager(String corpusPath, String savingPostingFilePath, boolean stemmer) {
        CorpusPath = corpusPath;
        SavingPath = savingPostingFilePath;
        isStemmer = stemmer;
        Dictionary = new ConcurrentHashMap<>();
        EntitiesDictionary = new ConcurrentHashMap<>();
        myComparator = (o1, o2) -> {
            int termid1,termid2;
            termid1 = o1.indexOf(";");
            termid2 = o2.indexOf(";");
            if (Integer.valueOf(o1.substring(0,termid1)) == Integer.valueOf(o2.substring(0,termid2))) {
                if (Integer.valueOf(o1.substring(termid1 + 1,o1.indexOf(";",termid1 + 1))) > Integer.valueOf(o2.substring(termid2 + 1,o2.indexOf(";",termid2 + 1))))
                    return 0;
                else
                    return 1;
            } else if (Integer.valueOf(o1.substring(0,termid1)) > Integer.valueOf(o2.substring(0,termid2)))
                return 0;
            else
                return 1;
        };
    }

    public void run() {
        Indexer = new Indexer(CorpusPath, SavingPath, isStemmer, 100, 900, Dictionary, EntitiesDictionary);
        Indexer.Index();
        while (Indexer.isActive()) ;
        TermID = Indexer.getTermID();
        DocID = Indexer.getDocID();
        FileID = Indexer.getFileID();
        Indexer = new Indexer(CorpusPath, SavingPath, isStemmer, 100, 915, Dictionary, EntitiesDictionary);
        Indexer.setAtomicIntegers(TermID.get(), DocID.get(), FileID.get());
        Indexer.Index();

    }

    private void SortAndCreate() {
        File file = new File(SavingPath);
        File[] files = file.listFiles();
        LinkedList<String> currentData = new LinkedList<>();
        List<String> list;
        for (int i = 0; i < 900; i++) {
            try {
                list = Files.readAllLines(files[i].toPath(), StandardCharsets.UTF_8);
                currentData.addAll(list);
                FileUtils.deleteDirectory(files[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(currentData,myComparator);
        int postingFileName = 0;
        StringBuilder toWrite = new StringBuilder();
        for (String tuple:currentData) {
            if (Integer.valueOf(tuple.substring(0,tuple.indexOf(";")))/15000 < postingFileName)
                toWrite.append(tuple + "\n");
            else {
                file = new File(SavingPath + postingFileName + ".txt");
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(toWrite.toString());
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toWrite.setLength(0);
                postingFileName++;
            }
        }

    }
}
