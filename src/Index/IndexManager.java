package Index;

import Rules.Token;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class IndexManager {
        private ConcurrentHashMap<Token, MutablePair<Integer, Integer>> Dictionary;//term-#doc-termID
    private ConcurrentHashMap<Token, ArrayList<Integer>> EntitiesDictionary;
    private boolean isStemmer;
    private String CorpusPath;
    private String SavingPath;
    private Indexer Indexer;
    private AtomicInteger TermID;
    private AtomicInteger DocID;
    private AtomicInteger FileID;
    private AtomicInteger TempPosingFileName;
    private Comparator<String> myComparator;
    private File file;
    private int Intervals;
    private int CorpusSize;
    private int DictionarySize;

    public IndexManager(String corpusPath, String savingPostingFilePath, boolean stemmer, int corpusSize) {
        CorpusPath = corpusPath;
        SavingPath = savingPostingFilePath;
        isStemmer = stemmer;
        Dictionary = new ConcurrentHashMap<>();
        EntitiesDictionary = new ConcurrentHashMap<>();
        myComparator = (o1, o2) -> {
            int termid1, termid2;
            termid1 = o1.indexOf(";");
            termid2 = o2.indexOf(";");
            int termIdComparing = Integer.compare(Integer.valueOf(o1.substring(0, termid1)), Integer.valueOf(o2.substring(0, termid2)));
            if (termIdComparing != 0)
                return termIdComparing;
            int docIdComparing = Integer.compare(Integer.valueOf(o1.substring(termid1 + 1, o1.indexOf(";", termid1 + 1)))
                    , Integer.valueOf(o2.substring(termid2 + 1, o2.indexOf(";", termid2 + 1))));
            return docIdComparing;
        };
        TermID = new AtomicInteger();
        DocID = new AtomicInteger();
        FileID = new AtomicInteger();
        TempPosingFileName = new AtomicInteger();
        CorpusSize = corpusSize;
        if (CorpusSize < 5)
            Intervals = 0;
        else {
            Intervals = (int) (0.0015 * corpusSize) + 1;
        }
    }

    public void run() {
        int pointerToRead = -1;
        if (CorpusSize < 5)
            pointerToRead = CorpusSize - 1; // -1 because the stop words need to be in the corpus path
        else
            pointerToRead = CorpusSize/2;
        Indexer = new Indexer(CorpusPath, SavingPath, isStemmer, 12, 0, pointerToRead, Dictionary, EntitiesDictionary);
        Indexer.setIntervals(Intervals);
        Indexer.Index();
        while (Indexer.isActive()) ;//busy waiting
        TermID.set(Indexer.getTermID().get());
        DocID.set(Indexer.getDocID().get());
        FileID.set(Indexer.getFileID().get());
        TempPosingFileName.set(Indexer.getTempPosingFileName().get());
        if (CorpusSize > 5) {
            Indexer = new Indexer(CorpusPath, SavingPath, isStemmer, 12, pointerToRead, CorpusSize, Dictionary, EntitiesDictionary);
            Indexer.setIntervals(Intervals);
            Indexer.setAtomicIntegers(TermID.get(), DocID.get(), FileID.get(), TempPosingFileName.get());
            Indexer.Index();
            while (Indexer.isActive()) ;//busy waiting
            DocID.set(Indexer.getDocID().get());
        }
        //sort dictionary before writing it to the disk after merging dictionary and entities//
        for (Token entity : EntitiesDictionary.keySet()) {
            if (entity != null && EntitiesDictionary.get(entity) != null
                    && EntitiesDictionary.get(entity).get(0) != null && EntitiesDictionary.get(entity).get(1) != null
                    && EntitiesDictionary.get(entity).get(0) > 1) {
                //check if the word is already in the dictionary
                entity.setName(entity.getName().toLowerCase());
                if (!Dictionary.containsKey(entity)){
                    entity.setName(entity.getName().toUpperCase());
                    MutablePair<Integer, Integer> pair = new MutablePair<>(EntitiesDictionary.get(entity).get(0), EntitiesDictionary.get(entity).get(1));
                    Dictionary.put(entity, pair);
                }
            }
        }
        EntitiesDictionary.clear();
        SortAndWriteDictionary(Dictionary);
        SortDocLexicon();
        this.DictionarySize = Dictionary.size();
        Dictionary.clear();
        SortAndCreate();
    }

    /**
     * Sort DocIDLexicon file by docID
     */
    private void SortDocLexicon() {
        File file = new File(SavingPath+"\\DocIDLexicon.txt");
        Comparator<String> myComp = (o1, o2) -> {
            String[] data1 = o1.split(";");
            String[] data2 = o2.split(";");
            int id1 = Integer.parseInt(data1[0]);
            int id2 = Integer.parseInt(data2[0]);
            return Integer.compare(id1,id2);
        };

        try {
            List<String> allDocs = Files.readAllLines(file.toPath());
            allDocs.sort(myComp);
            file.delete();
            FileWriter fileWriter = new FileWriter(file,true);
            for (String s:allDocs) {
                if (s != null && s.length() > 0 ) {
                    fileWriter.write(s + "\n");
                    fileWriter.flush();
                }
            }
                fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function sort the terms by term ID and then merges the files into posting files
     * Creates new directory postings which will contain all posting files
     */
    public void SortAndCreate() {
        if (Intervals == 0)
            Intervals = 1;
        file = new File(SavingPath);
        ArrayList<String> currentData = new ArrayList<>();
        File directory = new File(SavingPath + "\\postings");
        directory.mkdir();
        int postingFileName = 0;
        StringBuilder toWrite = new StringBuilder();
        LinkedList<String> writingList = new LinkedList<>();
        int countOfFiles = (new File(SavingPath).list().length - 2);
        for (int i = 0; i < 6; i++) {
            int numOfFilesToRead = (CorpusSize/Intervals/6);
            if (numOfFilesToRead == 0)
                numOfFilesToRead = CorpusSize;
            toWrite.setLength(0);
            currentData.clear();
            writingList.clear();
            if (i < 5)
                getChunkOfFiles(currentData, numOfFilesToRead*i, numOfFilesToRead *(i+1));
            else
                getChunkOfFiles(currentData,numOfFilesToRead*i,countOfFiles);
            currentData.sort(myComparator);
            postingFileName = 0;
            int counter = 0;
            for (String tuple : currentData) {
                if (Integer.valueOf(tuple.substring(0, tuple.indexOf(";"))) / 250 <= postingFileName) {
                    writingList.add(tuple);
                    counter++;
                } else {
                    if (writingList.size() > 0) {
                        file = new File(SavingPath + "\\postings\\" + postingFileName + ".txt");
                        try {
                            if (file.exists()) {
                                writingList.addAll(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
                                writingList.sort(myComparator);
                                FileUtils.forceDelete(file);
                            }
                            for (String string : writingList) {
                                toWrite.append(string + "\n");
                            }
                            FileWriter fileWriter = new FileWriter(file, true);
                            fileWriter.write(toWrite.toString());
                            fileWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        toWrite.setLength(0);
                        writingList.clear();
                    }
                    postingFileName++;
                }
            }
        }
    }

    /**
     * @param currentData all strings data from the files
     * @param start       from where to read
     * @param end         untill where to read
     */
    private void getChunkOfFiles(ArrayList<String> currentData, int start, int end) {
        List<String> list = null;
        for (int i = start; i < end && i < CorpusSize; i++) {
            try {
                file = new File(SavingPath + "\\" + i + ".txt");
                if (file.exists()) {
                    list = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                    currentData.addAll(list);
                    FileUtils.forceDelete(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        currentData.sort(myComparator);
    }

    public void SortAndWriteDictionary(ConcurrentHashMap<Token, MutablePair<Integer, Integer>> Dictionary) {
        LinkedHashMap<Token, MutablePair<Integer, Integer>> SortedDictionary = new LinkedHashMap<>();
        Dictionary.entrySet().stream().sorted(new Comparator<Map.Entry<Token, MutablePair<Integer, Integer>>>() {
            @Override
            public int compare(Map.Entry<Token, MutablePair<Integer, Integer>> tokenMutablePairEntry, Map.Entry<Token, MutablePair<Integer, Integer>> t1) {
                return tokenMutablePairEntry.getKey().getName().compareToIgnoreCase(t1.getKey().getName());
            }
        }).forEachOrdered(x -> SortedDictionary.put(x.getKey(), x.getValue()));

        File file = new File(SavingPath + "\\SortedDictionary.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(this.CorpusSize + "| \n" );
            int i = 0;
            StringBuilder data = new StringBuilder();
            for (HashMap.Entry<Token, MutablePair<Integer, Integer>> token : SortedDictionary.entrySet()
            ) {
                if (token.getKey().getName() != null || token.getKey().getName().length() > 0 || !token.getKey().getName().isEmpty()) {
                    data.append(token.getKey().getName() + "\t\t" + token.getValue().left + "\t\t" + token.getValue().right + "\n");
                }
            }
            fileWriter.write(data.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDictionarySize() {
        return this.DictionarySize;
    }

    public int getDocID() {
        return DocID.get();
    }

    public void setCorpusSize(int fileCount) {
        this.CorpusSize = fileCount;
    }
}
