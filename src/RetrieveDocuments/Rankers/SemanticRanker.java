package RetrieveDocuments.Rankers;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;
import RetrieveDocuments.Rankers.Ranker;

import java.util.ArrayList;
import java.util.Comparator;

public class SemanticRanker extends Ranker {

    private double avgDocLength;

    public SemanticRanker(ArrayList<Document> allDocs, ArrayList<Term> m_Query, long N, double averageDocLen) {
        super(allDocs, m_Query, N);
        avgDocLength = averageDocLen;
        DocsComparator = Comparator.comparingDouble(o -> o.getRank(Document.RankType.Semantic));
    }

    @Override
    public ArrayList<Document> Rank() {
        for (Document doc: allDocs) {
            double sumByFormula = 0;
            for (int i = 0; i <m_Query.size(); i++) {
                if (doc.isTermExist(m_Query.get(i)) && i+1<m_Query.size() && doc.isTermExist(m_Query.get(i+1))) {
                    try {
                        Term term1 = doc.getTermByName(m_Query.get(i));
                        Term term2 = doc.getTermByName(m_Query.get(i));
                        int numOfInstances = CountPositions(term1,term2);
                        double formula = ((2/m_Query.size())*numOfInstances);//avgDocLength;
                        sumByFormula += formula;
                    }catch (NullPointerException e) {
                        System.out.println("Null returned from Semantic Ranker");
                    }
                }
            }
            doc.setRank(Document.RankType.Semantic,sumByFormula);
            rankedDocs.add(doc);
        }
        rankedDocs.sort(DocsComparator);
        return rankedDocs;
    }

    /**
     *
     * @param term1
     * @param term2
     * @return total number of term1&term2 appearance together in the current doc
     */
    private int CountPositions(Term term1, Term term2) {
        int[] pos_Term1 = term1.getPositions();
        int[] pos_Term2 = term2.getPositions();
        int counter = 0;
        for (int i = 0; i < pos_Term1.length; i++) {
            for (int j = 0; j < pos_Term2.length; j++) {
                if (pos_Term2[j] - pos_Term1[i] == 1)
                    counter++;
            }
        }
        return counter;
    }
}
