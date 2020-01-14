package RetrieveDocuments.Rankers;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Ranker implements IRanker {

    protected ArrayList<Document> allDocs;
    protected ArrayList<Document> rankedDocs;
    protected ArrayList<Term> m_Query;
    protected long NumOfDocs;
    protected Comparator<Document> DocsComparator;
    protected final double log2 = Math.log(2);

    //N - total num of docs
    public Ranker(ArrayList<Document> allDocs, ArrayList<Term> m_Query, long N) {
        this.allDocs = allDocs;
        this.m_Query = m_Query;
        rankedDocs = new ArrayList<>();
        NumOfDocs = N;
    }

    protected double idf(long df) {
        return getLog2((double) NumOfDocs / df);
    }

    protected double getLog2(double x) {
        return Math.log(x);
    }


}
