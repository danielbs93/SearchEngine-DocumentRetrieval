package RetrieveDocuments;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;

import java.util.ArrayList;
import java.util.Comparator;

public class IdentifyDominantEntities {

    private Document document;
    private ArrayList<Term> entities;
    private Comparator<Term> tfComparator;

    public IdentifyDominantEntities(Document Doc) {
        document = Doc;
        entities = new ArrayList<>();
        tfComparator = Comparator.comparingDouble(Term::getTf);
    }

    public ArrayList<Term> get5DominantEntities() {
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


}
