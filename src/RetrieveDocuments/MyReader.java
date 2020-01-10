package RetrieveDocuments;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyReader {
    private String path;
    private File file;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private int CorpusSize;

    public MyReader(String savingPath) {
        this.path = savingPath;
    }

    /**
     * The formula of putting termID under posting file: termID/300
     * @param postingNumber after calculating the formula of putting termID under a txt posting file
     * @return
     */
    public List<String> readPostingFile(int postingNumber) {
        String postingFilePath = path + "\\postings\\" + postingNumber + ".txt";
        File posting = new File(postingFilePath);
        try {
            return Files.readAllLines(posting.toPath());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * DictionaryFormat: termName;df;termID
     * @return hashmap with termName as key and ArrayList of string to the rest of the data above
     */
    public HashMap<String, ArrayList<String>> loadDictionary() {
        HashMap<String,ArrayList<String>> Dictionary = new HashMap<>();
        file = new File(path+"\\SortedDictionary.txt");
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            line = bufferedReader.readLine();
            CorpusSize = Integer.parseInt(line.substring(0,line.indexOf(" ")));
            while ((line = bufferedReader.readLine()) != null) {
                String[] tuple = line.split("\t\t");
                ArrayList<String> dfAndTermID = new ArrayList<>();
                dfAndTermID.add(tuple[1]);
                dfAndTermID.add(tuple[2]);
                Dictionary.put(tuple[0],dfAndTermID);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Dictionary;
    }

    /**
     * docLexiconFormat: docID;docNO;fileNO;maxTF;uniqueWords;docLen
     * @return hashmap with docID as key and arraylist of strings the rest of the data mentioned above
     */
    public HashMap<Integer,ArrayList<String>> loadDocLexicon() {
        HashMap<Integer,ArrayList<String>> allDocs = new HashMap<>();
        file = new File(path+"\\DocIDLexicon.txt");
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tuple = line.split(";");
                ArrayList<String> data = new ArrayList<>();
                int docID = Integer.parseInt(tuple[0]);
                data.add(tuple[1]);
                data.add(tuple[2]);
                data.add(tuple[3]);
                data.add(tuple[4]);
                data.add(tuple[5]);
                allDocs.put(docID,data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allDocs;
    }

    public int getCorpusSize() {
        return CorpusSize;
    }
}
