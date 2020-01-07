package RetrieveDocuments.Rankers;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;
import RetrieveDocuments.Rankers.IRanker;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Ranker implements IRanker {

    protected ArrayList<Document> allDocs;
    protected ArrayList<Document> rankedDocs;
    protected ArrayList<Term> m_Query;
    protected long m_CorpusSize;
    protected Comparator<Document> DocsComparator;
    protected final double log2 = Math.log(2);

    public Ranker(ArrayList<Document> allDocs, ArrayList<Term> m_Query, long corpusSize) {
        this.allDocs = allDocs;
        this.m_Query = m_Query;
        rankedDocs = new ArrayList<>();
        m_CorpusSize = corpusSize;
    }

    protected double idf(long df) {
        return getLog2((double) m_CorpusSize / df);
    }

    protected double getLog2(double x) {
        return Math.log(x) / log2;
    }


}
