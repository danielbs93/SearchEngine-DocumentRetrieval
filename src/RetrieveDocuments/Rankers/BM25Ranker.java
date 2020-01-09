package RetrieveDocuments.Rankers;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;

import java.util.ArrayList;

public class BM25Ranker extends Ranker {

    //Control parameters
    private double k;
    private double b;
    private double avgDocLen;

    public BM25Ranker(ArrayList<Document> allDocs, ArrayList<Term> m_Query, long corpusSize, double averageDocLength) {
        super(allDocs, m_Query, corpusSize);
        avgDocLen = averageDocLength;
        //Compare by BM25 rank
        DocsComparator = (o1, o2) -> {
            double rank1 = o1.getRank(Document.RankType.BM25);
            double rank2 = o2.getRank(Document.RankType.BM25);
            return Double.compare(rank1, rank2);
        };
        //Defaults values ->usually set to those values
        k = 1.2;
        b = 0.75;
    }

    @Override
    public ArrayList<Document> Rank() {
        for (Document doc : allDocs) {
            doc.setRank(Document.RankType.BM25, BM25(doc));
            rankedDocs.add(doc);
        }
        rankedDocs.sort(DocsComparator);
        return this.rankedDocs;
    }

    /**
     * calculate BM25-okapi by its formula
     * @param document
     * @return rank of document by BM25's rank assessment
     * !!tf here is not normalized by maxtf
     */
    private double BM25(Document document) {
        double sumBM25 = 0;
        double constantPartDenominator = k * (1 - b + (b * (document.getDocLength() / avgDocLen)));
        for (Term term : m_Query) {
            if (document.isTermExist(term)) {
                Term found = document.getTerm(term);
                double idf = idf(found.getDf());
                double tf = found.getTf();
                sumBM25 += idf*((tf*(k+1))/(tf+constantPartDenominator));
            }
        }
        return sumBM25;
    }


}
