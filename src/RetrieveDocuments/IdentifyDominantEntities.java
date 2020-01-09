package RetrieveDocuments;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;
import Rules.Token;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class IdentifyDominantEntities {

    private Document document;
    private ArrayList<Term> entities;
    private Comparator<Term> tfComparator;
    private String corpusPath;

    public IdentifyDominantEntities(Document Doc, String corpus) {
        document = Doc;
        entities = new ArrayList<>();
        tfComparator = Comparator.comparingDouble(Term::getTf);
        corpusPath = corpus;
    }

    public ArrayList<Term> get5DominantEntities(HashMap<String,ArrayList<String>> dictionary) {
        ArrayList<Term> allTerms = document.getDocTerms();
        ArrayList<Term> allEntities = new ArrayList<>();
        for (Term term:allTerms) {
            if (term.getIsEntity() == 1)
                allEntities.add(term);
        }
        allEntities.sort(tfComparator);
        if (allEntities.size() < 5)
            entities = allEntities;
        else {
            for (int i = 0; i < 5; i++) {
                entities.add(allEntities.get(i));
            }
        }
        return entities;
    }

    public ArrayList<Token> parseDoc() {
        File file = new File(corpusPath+ "\\" + document.getFileNO() + "\\" +document.getFileNO());
        String doc = "";
        try {
            List<String> allLines = Files.readAllLines(file.toPath());
            for (String docno:allLines) {
                if (docno.contains(document.getDocNO())) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
