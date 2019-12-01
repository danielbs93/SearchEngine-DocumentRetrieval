import java.io.*;
import java.util.LinkedList;

public class ReadFile {
    private BufferedReader Bfr;
    private File CorpusFolder;
    private LinkedList<String> Documents;
    private File[] AllFiles;
    private int FilePointer;
    private int DocPointer;

    public ReadFile(String path) {
        CorpusFolder = new File(path);
        Documents = new LinkedList<>();
        FilePointer = 0;
        DocPointer = 0;
        AllFiles = CorpusFolder.listFiles();

    }

    /**
     * Hierarchy is: Corpus Directory within subfolders which each sub folder holds 1 file
     * that contains list of documents.
     * @return next file in the next directory
     */
    private void getNextFile() {
        File nextFolder = AllFiles[FilePointer];
        FilePointer++;
        File[] files = nextFolder.listFiles();
        try {
            Bfr = new BufferedReader(new FileReader(files[0]));
            CreateDocumentsFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creating the Documents list in the current directory
     * @throws IOException
     */
    private void CreateDocumentsFiles() throws IOException {
        String line, doc;
        while ((line = Bfr.readLine()) != null ) {
            if (line.equals("<DOC>")) {
                doc = line;
                while (!(line = Bfr.readLine()).equals("</DOC>"))
                    doc = doc + " " + line;
                doc = doc + " </DOC>";
                Documents.add(doc);
                doc = "";
            }
        }
    }

    /**
     * FileReader class will return the next document available in the file
     * If all documents in the file has been read we will get next file
     * @return
     */
    public String getNextDoc() {
        if (Documents.isEmpty())
            getNextFile();
        String document = Documents.remove(DocPointer);
        DocPointer++;
        return document;
    }


}
