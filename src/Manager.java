import Rules.Token;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private File file;

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
            int termIdComparing = Integer.compare(Integer.valueOf(o1.substring(0,termid1)),Integer.valueOf(o2.substring(0,termid2)));
            if (termIdComparing != 0)
                return termIdComparing;
            int docIdComparing = Integer.compare(Integer.valueOf(o1.substring(termid1 + 1,o1.indexOf(";",termid1 + 1)))
                    ,Integer.valueOf(o2.substring(termid2 + 1,o2.indexOf(";",termid2 + 1))));
            return docIdComparing;
        };
    }

    public void run() {
        Indexer = new Indexer(CorpusPath, SavingPath, isStemmer, 100, 0,900, Dictionary, EntitiesDictionary);
        Indexer.Index();
        while (Indexer.isActive()) ;
        TermID = Indexer.getTermID();
        DocID = Indexer.getDocID();
        FileID = Indexer.getFileID();
        Indexer = new Indexer(CorpusPath, SavingPath, isStemmer, 100, 900,1815, Dictionary, EntitiesDictionary);
        Indexer.setAtomicIntegers(TermID.get(), DocID.get(), FileID.get());
        Indexer.Index();

    }

    public void SortAndCreate() {
        file = new File(SavingPath);
//        File[] files = file.listFiles();
        ArrayList<String> currentData = new ArrayList<>();
        getChunkOfFiles(currentData,0,900);
        Collections.sort(currentData,myComparator);

        int postingFileName = 0;
        StringBuilder toWrite = new StringBuilder();
        int counter = 0;
        for (String tuple:currentData) {
            if (Integer.valueOf(tuple.substring(0,tuple.indexOf(";")))/1500 < postingFileName) {
                toWrite.append(tuple + "\n");
                counter++;
            }
            else {
                file = new File(SavingPath + "\\" + postingFileName + ".txt");
                try {
                    FileWriter fileWriter = new FileWriter(file,true);
                    fileWriter.write(toWrite.toString());
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toWrite.setLength(0);
                postingFileName = Integer.valueOf(currentData.get(counter).substring(0,currentData.get(counter).indexOf(";")));
            }
        }
        toWrite.setLength(0);
        currentData.removeAll(currentData);
        getChunkOfFiles(currentData,900,1815);
        Collections.sort(currentData,myComparator);
        String first = currentData.get(0);
        postingFileName = Integer.valueOf(first.substring(0,first.indexOf(";")))/15000;
        LinkedList<String> writingList = new LinkedList<>();
        counter = 0;
        for (String tuple:currentData) {
            if (Integer.valueOf(tuple.substring(0,tuple.indexOf(";")))/15000 < postingFileName) {
                writingList.add(tuple);
                counter++;
            }
            else {
                file = new File(SavingPath + "\\" + postingFileName + ".txt");
                try {
                    if (file.exists()) {
                        writingList.addAll(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
                        Collections.sort(writingList,myComparator);
                        FileUtils.forceDelete(file);
                    }
                    for (String string:writingList) {
                        toWrite.append(string);
                    }
                    FileWriter fileWriter = new FileWriter(file,true);
                    fileWriter.write(toWrite.toString());
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toWrite.setLength(0);
                writingList.removeAll(writingList);
                postingFileName = Integer.valueOf(currentData.get(counter).substring(0,currentData.get(counter).indexOf(";")));
            }
        }


    }

    /**
     * @param currentData all strings data from the files
     * @param start from where to read
     * @param end untill where to read
     */
    private void getChunkOfFiles(ArrayList<String> currentData, int start, int end) {
        List<String> list = null;
        for (int i = start; i < end; i++) {
            try {
                file = new File(SavingPath + "\\" + i + ".txt");
                list = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                currentData.addAll(list);
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
