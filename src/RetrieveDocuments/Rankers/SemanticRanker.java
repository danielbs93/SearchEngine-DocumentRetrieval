package RetrieveDocuments.Rankers;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;
import RetrieveDocuments.Rankers.Ranker;

import java.util.ArrayList;

public class SemanticRanker extends Ranker {


    public SemanticRanker(ArrayList<Document> allDocs, ArrayList<Term> m_Query, long corpusSize) {
        super(allDocs, m_Query, corpusSize);
    }

    @Override
    public ArrayList<Document> Rank() {
        return null;
    }
}
