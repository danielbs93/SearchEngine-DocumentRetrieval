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
    private Comparator<Term> tfComparator;
    private String corpusPath;
    private boolean isStemmer;

    public IdentifyDominantEntities(String corpusPath, boolean isStemmer) {
        this.corpusPath = corpusPath;
        this.isStemmer = isStemmer;
        entities = new ArrayList<>();
        tfComparator = Comparator.comparingDouble(Term::getTf);
    }

    public ArrayList<Term> get5DominantEntities(HashMap<String, ArrayList<String>> dictionary) {
        ArrayList<Token> parsedEntites = parseDoc();
        ArrayList<Term> allEntities = new ArrayList<>();
//        ArrayList<Term> allTerms = document.getDocTerms();
        for (Token token : parsedEntites) {
            if (dictionary.keySet().contains(token.getName())) {
                int id = Integer.parseInt(dictionary.get(token.getName()).get(1));
                Term term = new Term(token.getName(),id);
                term.setTf(token.getTf());
                allEntities.add(term);
            }
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
        File file = new File(corpusPath + "\\" + document.getFileNO() + "\\" + document.getFileNO());
        StringBuilder doc = new StringBuilder();
        try {
            ArrayList<String> allLines = (ArrayList<String>) Files.readAllLines(file.toPath());
            boolean done = false;
            for (int i = 0; i < allLines.size(); i++) {
                if (allLines.get(i).contains(document.getDocNO())) {
                    for (int j = i + 1; j < allLines.size(); j++) {
                        if (allLines.get(j).contains("<TEXT>")) {
                            for (int k = j + 1; k < allLines.size(); k++) {
                                if (allLines.get(k).contains("</TEXT>")) {
                                    done = true;
                                    break;
                                }
                                doc.append(allLines.get(k) + "\n");
                            }
                        }
                        if (done)
                            break;
                    }
                }
                if (done)
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parser p = new Parser(doc.toString(), corpusPath, isStemmer);
        ArrayList<Token> allEntities = p.Parse(true)[1];
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
