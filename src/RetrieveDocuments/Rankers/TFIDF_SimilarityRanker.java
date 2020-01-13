package RetrieveDocuments.Rankers;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;
import RetrieveDocuments.Rankers.Ranker;

import java.util.ArrayList;

public class TFIDF_SimilarityRanker extends Ranker {


    public TFIDF_SimilarityRanker(ArrayList<Document> allDocs, ArrayList<Term> m_Query, long N) {
        super(allDocs, m_Query, N);

        //compare by Cosine Similarity - TFIDF rank
        DocsComparator = (o1, o2) -> {
            double rank1 = o1.getRank(Document.RankType.TFIDF);
            double rank2 = o2.getRank(Document.RankType.TFIDF);
            return Double.compare(rank1, rank2);
        };
    }

    /**
     * @return list of ranked docs sorted by their TFIDF rank
     */
    @Override
    public ArrayList<Document> Rank() {
        for (Document doc : allDocs) {
            doc.setRank(Document.RankType.TFIDF, Sim(doc));
            rankedDocs.add(doc);
        }
        rankedDocs.sort(DocsComparator);
        return this.rankedDocs;
    }

    private double rank(int tf, int maxtf, long df) {
        if (tf == 0) return 0.0;
        return (tf / maxtf) * super.idf(df);
    }

    /**
     * @param doc which we are creating inner product for ranking
     * @return ranked similarity between the document and the query
     */
    private double Sim(Document doc) {
        double sum = 0;
        for (Term term : m_Query) {
            if (doc.isTermExist(term)) {
                Term found = doc.getTerm(term);
                sum += rank(found.getTf(), doc.getMaxTfTerm(), found.getDf());
            }
        }
        return sum;
    }

//    /**
//     * @param document which we will evaluate the rank by CosSimilarity
//     * @return Cosine Similarity
//     */
//    private double CosSim(Document document) {
//        double sumWeightedTerms = 0, sumWeightedQuery = 0;
//        sumWeightedQuery = m_Query.size();
//        for (Term term : m_Query) {
//            if (document.isTermExist(term)) {
//                Term found = document.getTerm(term);
//                sumWeightedTerms += Math.pow(rank(found.getTf(), document.getMaxTfTerm(), found.getDf()), 2);
//            }
//        }
//        double denominator = Math.sqrt(sumWeightedQuery * sumWeightedTerms);
//        return Sim(document) / denominator;
//    }
}
