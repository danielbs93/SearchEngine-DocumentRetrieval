package RetrieveDocuments.Rankers;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;

import java.util.ArrayList;
import java.util.Comparator;

public class BM25Ranker extends Ranker {

    //Control parameters
    private double k;
    private double b;
    private double avgDocLen;
    private double finalRankBM25;

    public BM25Ranker(ArrayList<Document> allDocs, ArrayList<Term> m_Query, long N, double averageDocLength) {
        super(allDocs, m_Query, N);
        avgDocLen = averageDocLength;
        //Compare by BM25 rank
        DocsComparator = Comparator.comparingDouble(o ->o.getRank(Document.RankType.BM25));
//                (o2, o1) -> {
//            double rank1 = o1.getRank(Document.RankType.BM25);
//            double rank2 = o2.getRank(Document.RankType.BM25);
//            return Double.compare(rank1, rank2);
//        };
        //Defaults values ->usually set to those values
        k = 1.13;
        b = 0.523;
        // k - 1.23 b - 0.00023 = 134
        // k - 1.23 b - 0.75 = 140
        // k - 1.1 b - 0.75 = 141
        // k - 1.023 b - 0.523 = 142
        // k - 1.4 b - 0.523 = 144
        // k - 1.423 b - 0.523 = 145
    }

    @Override
    public ArrayList<Document> Rank() {
        for (Document doc : allDocs) {
            doc.setRank(Document.RankType.BM25, BM25(doc));
            doc.setFinalRank(finalRankBM25);
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
                finalRankBM25 += 0.5*idf*((tf*(k+1))/(tf+constantPartDenominator)) + 0.25*TermIn10PercentFromDoc(found, document.getDocLength()) + 0.25*found.getIsEntity();
            }
        }
        return sumBM25;
    }

    private int TermIn10PercentFromDoc(Term found, int docLength) {
        int counterTop10 = 0;
        double top10 = docLength*0.1;
        int position = 0;
        for (int current_position: found.getPositions()) {
            position += current_position;
            if (position < top10)
                counterTop10++;
        }
        return counterTop10;
    }



}
