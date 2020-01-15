package RetrieveDocuments;

import Index.Parser;
import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;
import Rules.Token;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class IdentifyDominantEntities {

    private Document document;
    private ArrayList<Term> entities;
    private Comparator<Term> dominantComparator;
    private String corpusPath;
    private boolean isStemmer;

    public IdentifyDominantEntities(String corpusPath, boolean isStemmer) {
        this.corpusPath = corpusPath;
        this.isStemmer = isStemmer;
        entities = new ArrayList<>();
        dominantComparator = Comparator.comparingDouble(o1 -> ((o1.getTf())/(o1.getDf())));
    }

    public ArrayList<Term> get5DominantEntities(HashMap<String, ArrayList<String>> dictionary) {
        ArrayList<Token> parsedEntites = parseDoc();
        ArrayList<Term> allEntities = new ArrayList<>();
        for (Token token : parsedEntites) {
            if (dictionary.keySet().contains(token.getName())) {
                int id = Integer.parseInt(dictionary.get(token.getName()).get(1));
                int df = Integer.parseInt(dictionary.get(token.getName()).get(0));
                Term term = new Term(token.getName(),id);
                term.setTf(token.getTf());
                term.setDf(df);
                allEntities.add(term);
            }
        }
        allEntities.sort(dominantComparator);
        if (allEntities.size() < 5)
            entities = allEntities;
        else {
            //returning only 5 ranked entities
            for (int i = 0; i < 5; i++) {
                entities.add(allEntities.get(i));
            }
        }
        document.setDominantEntities(entities);
        return entities;
    }

    public ArrayList<Token> parseDoc() {
        File file = new File(corpusPath + "\\" + document.getFileNO() + "\\" + document.getFileNO());
        StringBuilder doc = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine() ;
            boolean done = false;
            while (line!=null && !done){
                if (line.contains(document.getDocNO())){
                    line = line.substring(7,line.length() - 8);
                    line = line.replaceAll(" ","");
                    if (line.equals(document.getDocNO())) {
                        while (!(line = bufferedReader.readLine()).contains("<TEXT>")) {
                        }
                        line = bufferedReader.readLine();
                        doc.append(line + " ");
                        while (!(line = bufferedReader.readLine()).contains("</TEXT>")) {
                            doc.append(line + " ");
                        }
                        done = true;
                    }
                }
                else
                    line = bufferedReader.readLine();
            }
            fileReader.close();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parser p = new Parser(doc.toString(), corpusPath, isStemmer);
        ArrayList<Token> allEntities = p.Parse(false)[1];
        return CountAndRemove(allEntities);
    }

    private ArrayList<Token> CountAndRemove(ArrayList<Token> tokens) {
        ArrayList<Token> afterRemoving = new ArrayList<>();
        for (Token term : tokens) {
            for (Token token : tokens) {
                if (token != null && !term.isEqual(token) && !afterRemoving.contains(term)) {
                    if (term.getName().equals(token.getName())) {
                        term.increaseTF();
                        term.addPosition(token.getPosition());
                        afterRemoving.add(term);
                    }
                }
            }
        }
        tokens.removeAll(afterRemoving);
        tokens.addAll(afterRemoving);
        return tokens;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
