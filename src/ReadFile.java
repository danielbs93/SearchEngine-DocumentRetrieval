
import java.io.*;
import java.util.LinkedList;

/**
 * Created by Daniel Ben Simon & Eran Toutian
 */
public class ReadFile {
    private BufferedReader Bfr;
    private LinkedList<StringBuilder> Documents;
    private int DocPointer;
    private File curFile;
    private String DOCNO;
//    private File CorpusFolder;
//    private File[] AllFiles;
//    private int FilePointer;
//    private String nextPath;

    public ReadFile(String path) {
        curFile = new File(path);
        Documents = new LinkedList<>();
        DocPointer = 0;
        try {
            CreateDocumentsFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        FilePointer = 0;
//        AllFiles = CorpusFolder.listFiles();
//        nextPath = path;

    }

    /**
     * Hierarchy is: Corpus Directory within subfolders which each sub folder holds 1 file
     * that contains list of documents.
     * @return next file in the next directory
     */
//    private void getNextFile() {
//        int i = 0;
//        while (i < 10000) {
//
//        }
//        File nextFolder = AllFiles[FilePointer];
//        FilePointer++;
//        File[] files = nextFolder.listFiles();
//        try {
//            Bfr = new BufferedReader(new FileReader(files[0]));
//            CreateDocumentsFiles();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Creating the Documents list in the current directory
     * @throws IOException
     */
    private void CreateDocumentsFiles() throws IOException {
        String line;
        while ((line = Bfr.readLine()) != null ) {
            StringBuilder doc = new StringBuilder();
            if (line.equals("<DOC>")) {
                doc.append(line);
                while (!(line = Bfr.readLine()).equals("</DOC>"))
                    doc.append(" " + line);
                doc.append(" </DOC>");
                Documents.add(doc);
            }
        }
    }

    /**
     * FileReader class will return the next document available in the file
     * If all documents in the file has been read we will get next file
     * @return
     */
    public String getNextDoc() {
//        if (Documents.isEmpty())
//            getNextFile();
        StringBuilder document = Documents.remove(DocPointer);
//        DocPointer++;
        DOCNO = buildDocNO(document.toString());
        return document.toString();
    }

    public String getFileNO() {
        return curFile.getName();
    }

    public String getDocNO() {
        return DOCNO;
    }

    private String buildDocNO(String document) {
        int start = document.indexOf("<DOCNO>") + 7;
        int end = document.indexOf("</DOCNO>");
        String docNO = document.substring(start,end);
        docNO.replace(" ","");
        return docNO;
    }

    public boolean isEmpty() {
        return this.Documents.isEmpty();
    }


}
