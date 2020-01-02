
import Rules.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Daniel Ben Simon & Eran Toutian
 */
public class ReadFile {
    private BufferedReader Bfr;
    private ArrayList<StringBuilder> Documents;
    private int DocPointer;
    private File curFile;
    private String DOCNO;
    private FileReader fileReader;
    private int[] fileIDcounter;
    private int FilePointer;


    public ReadFile(File[] files,int fromWhereToRead, int numOfFilesToRead, int intervals) {
        Documents = new ArrayList<>();
        DocPointer = 0;
        FilePointer = 0;
        fileIDcounter = new int[intervals];
        try {
            int j = 0;
            for (int i = fromWhereToRead; i < numOfFilesToRead && i <files.length; i++) {
                curFile = files[i].listFiles()[0];
                fileReader = new FileReader(curFile);
                Bfr = new BufferedReader(fileReader);
                CreateDocumentsFiles(j);
                fileReader.close();
                Bfr.close();
                j++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    /**
     * Creating the Documents list in the current directory
     * @throws IOException
     * @param i
     */
    private void CreateDocumentsFiles(int i) throws IOException {
        String line;
        while ((line = Bfr.readLine()) != null ) {
            StringBuilder doc = new StringBuilder();
            if (line.equals("<DOC>")) {
                doc.append(line);
                while (!(line = Bfr.readLine()).equals("</DOC>"))
                    doc.append(" " + line);
                doc.append(" </DOC>");
                Documents.add(doc);
                fileIDcounter[i]++;
            }
        }
    }

    /**
     * FileReader class will return the next document available in the file
     * If all documents in the file has been read we will get next file
     * @return
     */
    public String getNextDoc() {
        StringBuilder document = Documents.remove(DocPointer);
        DOCNO = buildDocNO(document.toString());
        return document.toString();
    }

    public boolean DecreaseDocCounter() {
        if (fileIDcounter[FilePointer] == 0) {
            FilePointer++;
            fileIDcounter[FilePointer]--;
            return false;
        }
        else {
            fileIDcounter[FilePointer]--;
            if (fileIDcounter.length == 1 && fileIDcounter[FilePointer] == 0)
                return false;
            return true;
        }
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
