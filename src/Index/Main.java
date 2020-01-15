package Index;

import RetrieveDocuments.MyReader;
import RetrieveDocuments.RetrieveManager;
import Rules.Stemmer;
import Rules.Token;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        String CorpusPath = "C:\\Users\\erant\\Desktop\\corpus";
        String savingPath = "C:\\Users\\erant\\Desktop\\MNS";
//        IndexManager mng = new IndexManager(CorpusPath,savingPath,true,1815);
//        mng.run();
//        long endTime = System.currentTimeMillis();
//        System.out.println("Number of indexed docs:  " + mng.getDocID());
//        System.out.println("Number of unique terms (Dictionary size):  " + mng.getDictionarySize());
//        System.out.println("Time it took to build the Index:  " + (endTime - startTime)/1000);
//        System.out.println("Time it took to Merge&Sort posting files: " + (endTime - startTime)/1000);

        MyReader reader = new MyReader(savingPath);
        try {
            HashMap<String, ArrayList<String>> dictionary =  reader.loadDictionary();
            long startTime = System.currentTimeMillis();
            RetrieveManager retrieveManager = new RetrieveManager(false,true
                    ,false,true,"C:\\Users\\erant\\Desktop\\queries.txt"
                    ,CorpusPath,savingPath,dictionary);
            boolean found = retrieveManager.Start();
            long endTime = System.currentTimeMillis();
            System.out.println("Time it took: " + (endTime - startTime)/1000);
            if(!found) {
                System.out.println("Did not match any documents.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




//        writeTheSameWordInUpperandLower(savingPath);

//        SortDictionaryByTermID(savingPath);
//        calculateAVGDocLen(savingPath);
        //$1.1-million $1.2-billion $1.3-Million $1.4-Billion
//        String s = "<TEXT>  $2m $3M $4bn $5b $6.2B $74Bn </TEXT>";
//        Parser p = new Parser(s,CorpusPath,true);
//        ArrayList<Token> [] a = p.Parse();
//        System.out.println("");
    }

    private static void calculateAVGDocLen(String saving) {
        File file = new File(saving+"\\DocIDLexicon.txt");
        String trouble = "";
        try {
            List<String> list = Files.readAllLines(file.toPath());
            int sum = 0;
            for (String s: list) {
                String[] data = s.split(";");
                if (data.length > 4)
                    sum += Integer.parseInt(data[5]);
                trouble = data[0];
            }
            System.out.println(sum/list.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("DocID : " + trouble);
        }
    }

    private static void SortDictionaryByTermID(String savingPath) {
        File file = new File(savingPath+"\\SortedDictionary.txt");
        try {
            List<String> allTerms = Files.readAllLines(file.toPath());
            Comparator<String> myComp = new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String termID1 = o1.substring(o1.lastIndexOf("|") + 1);
                    String termID2 = o2.substring(o2.lastIndexOf("|") + 1);
                    int tID1 = Integer.valueOf(termID1);
                    int tID2 = Integer.valueOf(termID2);
                    if (tID1>tID2)
                        return 1;
                    if (tID2>tID1)
                        return -1;
                    else
                        return 0;
                }
            };

            allTerms.sort(myComp);
            file = new File(savingPath+"\\TermIDSortedDictionary.txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file,true);
            for (String s: allTerms) {
                fileWriter.write(s+"\n");
            }
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeTheSameWordInUpperandLower(String savingPath) {
        File file = new File(savingPath + "\\SortedDictionary.txt");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<String> list = Files.readAllLines(file.toPath());
            for (String line: list) {
                if (Character.isUpperCase(line.charAt(0))) {
                    for (String searchLowerCase:list) {
                        if (searchLowerCase.length() != 0 && !searchLowerCase.equals(line) && Character.isLowerCase(searchLowerCase.charAt(0))) {
                            String toUpper = StringUtils.upperCase(searchLowerCase);
                            if (toUpper.substring(0,toUpper.indexOf("|")).equals(line.substring(0,line.indexOf("|")))) {
                                stringBuilder.append(line + "\n");
                                stringBuilder.append(searchLowerCase + "\n");
                            }
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File file1 = new File(savingPath + "\\AllUpperWords.txt");
            file1.createNewFile();
            FileWriter fileWriter = new FileWriter(file1);
            fileWriter.write(stringBuilder.toString());
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
