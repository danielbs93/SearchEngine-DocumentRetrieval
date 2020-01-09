package Index;

import Rules.Stemmer;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        String CorpusPath = "C:\\Users\\USER\\Desktop\\הנדסת מערכות מידע\\שנה ג\\סמסטר ה\\אחזור\\SearchEngineProject\\Test\\corpus";
        String savingPath = "C:\\Users\\USER\\Desktop\\הנדסת מערכות מידע\\שנה ג\\סמסטר ה\\אחזור\\SearchEngineProject\\Test\\postingFiles";
        IndexManager mng = new IndexManager(CorpusPath,savingPath,true,100);
       mng.run();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mng.SortAndCreate();
        long endTime = System.currentTimeMillis();
        System.out.println("Number of indexed docs:  " + mng.getDocID());
        System.out.println("Number of unique terms (Dictionary size):  " + mng.getDictionarySize());
        System.out.println("Time it took to build the Index:  " + (endTime - startTime)/1000);
        System.out.printf("Time it took to Merge&Sort posting files: " + (endTime - startTime)/1000);

//        writeTheSameWordInUpperandLower(savingPath);

//        SortDictionaryByTermID(savingPath);
//        calculateAVGDocLen(savingPath);
//        String s = "<TEXT> $1.1-million $1.2-billion $1.3-Million $1.4-Billion $2m $3M $4bn $5b $6.2B $74Bn </TEXT>";
//        Parser p = new Parser(s,CorpusPath,true);
//        p.Parse();
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
