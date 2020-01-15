package RetrieveDocuments;

import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;
import RetrieveDocuments.Rankers.BM25Ranker;
import RetrieveDocuments.Rankers.Ranker;
import RetrieveDocuments.Rankers.SemanticRanker;

import javax.print.Doc;
import java.util.*;

public class Searcher {

    private ArrayList<ArrayList<Term>> queries;
    private ArrayList<Term> query;
    private ArrayList<Document> allDocs;//all docs that at least one term from the query appears on it
    private Queue<ArrayList<Document>> rankedDocs;
    private String savingPath;
    private MyReader myReader;
    private Ranker ranker;
    private boolean semantic;
    private HashMap<String, ArrayList<String>> m_Dicrionary;
    private HashMap<Integer, ArrayList<String>> m_DocLexicon;
    private int avgDocLength;
    private Comparator<Integer[]> CompareByTermID;
    private Comparator<Document> compareByFinalRank;


    public Searcher(String savingPath, boolean semntic, HashMap<String, ArrayList<String>> dictionary) {
        this.savingPath = savingPath;
        this.semantic = semntic;
        myReader = new MyReader(this.savingPath);
        allDocs = new ArrayList<>();
        rankedDocs = new LinkedList<>();
        m_Dicrionary = dictionary;
        m_DocLexicon = myReader.loadDocLexicon();
        CompareByTermID = Comparator.comparingInt(o -> o[0]);
        compareByFinalRank = Comparator.comparingDouble(o -> o.getFinalRank());
        avgDocLength = (int) getAvgDocLength();
    }

    public ArrayList<Document> Rank() {
        //coefficient for each rank method
        double alpha = 0.25, beta = 0.75;
        ArrayList<Document> rankedList = new ArrayList<>();
            ArrayList<Document> BM25ranked = new ArrayList<>();
            ArrayList<Document> SemanticRanked = new ArrayList<>();
            ranker = new BM25Ranker(allDocs, query, m_DocLexicon.size(), avgDocLength);
            rankedList = ranker.Rank();
            if (rankedList.size() > 0) {
                try {
                    if (rankedList.size() > 49)
                        BM25ranked = new ArrayList<>(rankedList.subList(rankedList.size() - 50, rankedList.size()));
                    else
                        BM25ranked = new ArrayList<>(rankedList.subList(0, rankedList.size()));

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                if (semantic) {
                    ranker = new SemanticRanker(allDocs, query, m_DocLexicon.size(), avgDocLength);
                    try {
                        rankedList = ranker.Rank();
                        SemanticRanked = new ArrayList<Document>(rankedList.subList(rankedList.size() - 50, rankedList.size()));
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    for (Document doc : allDocs) {
                        double bm25 = doc.getRank(Document.RankType.BM25);
                        double positionRank = doc.getRank(Document.RankType.Semantic);
                        if (semantic) {
                            doc.setFinalRank(alpha * bm25 + beta * positionRank);
                        }
                    }
                }
                rankedList.clear();
                if (!semantic) {
                    rankedList.addAll(BM25ranked);
                } else {
                    allDocs.sort(compareByFinalRank);
                    rankedList.addAll(allDocs.subList(allDocs.size()-50,allDocs.size()));
                }
            }
        return rankedList;

    }

    /**
     * @return average doc length
     */
    private double getAvgDocLength() {
        int sum = 0;
        for (ArrayList<String> arr : m_DocLexicon.values()) {
            int docLength = Integer.parseInt(arr.get(4));
            sum += docLength;
        }
        return sum / m_DocLexicon.keySet().size();
    }


    /**
     * This function is responsible to retrieve all documents that at least one term from the
     * query is appear on it.
     *
     * @param queriesFromFile
     */
    public void Search(ArrayList<ArrayList<Term>> queriesFromFile) {
        queries = queriesFromFile;
        Searcher searcher = new Searcher(this.savingPath,this.semantic,this.m_Dicrionary);
        //for each query from all queries
        for (ArrayList<Term> currentQuery : queries) {
            allDocs.clear();
            searcher.setQuery(currentQuery);
            //mapping the termID&df to the posting file name it belongs
            HashMap<Integer, ArrayList<Integer[]>> termIDandDF = BuildFromDictionary(currentQuery);
            //reading the posting file which all [currentTermID/300] belongs to this posting file
            for (Integer postingName : termIDandDF.keySet()) {
                //get all couples termID&DF that under the same posting file
                ArrayList<Integer[]> idAndDF = termIDandDF.get(postingName);
                List<String> postingFile = myReader.readPostingFile(postingName);
                CreateDocumentAndTerm(idAndDF, postingFile, currentQuery);
            }
            searcher.setAllDocs(allDocs);
            ArrayList<Document> result = searcher.Rank();
            Collections.reverse(result);
            rankedDocs.add(result);
        }
    }

    /**
     * This function responsible for creating terms objects after collecting the necessary information from posting file.
     * Also creating documents to allDocs object after insuring that the term in the query exist in the document and add it to it.
     *
     * @param idAndDF
     * @param postingFile
     * @param query
     */
    private void CreateDocumentAndTerm(ArrayList<Integer[]> idAndDF, List<String> postingFile, ArrayList<Term> query) {
        for (String tuple : postingFile) {
            String[] tupleData = tuple.split(";");
            //from posting file
            int termID = Integer.parseInt(tupleData[0]);
            int df;
            if ((df = FindTermIDAndGetDF(idAndDF, termID)) != -1) {
                int docID = Integer.parseInt(tupleData[1]);
                String termName = getTermName(query, termID);
                Document doc;
                if ((doc = getDoc(docID)) != null) {
                    doc.addTerm(new Term(tupleData, df, termName));
                } else {
                    ArrayList<String> data = m_DocLexicon.get(docID);
                    if (data == null)
                        try {
                            throw new Exception("Data received from DocLexicon is null");
                        } catch (Exception e) {
                            System.exit(-1);
                        }
                    doc = new Document(data, docID);
                    doc.addTerm(new Term(tupleData, df, termName));
                    allDocs.add(doc);
                }
            }
        }
    }

    /**
     * @param idAndDF
     * @param termID
     * @return the DF for the specific term ID given, if not found return -1
     */
    private int FindTermIDAndGetDF(ArrayList<Integer[]> idAndDF, int termID) {
        for (Integer[] couple : idAndDF) {
            if (couple[0] == termID)
                return couple[1];
        }
        return -1;
    }

    /**
     * @param query  which the term is appear in it
     * @param termID taken from the posting file
     * @return the term name if found or null
     */
    private String getTermName(ArrayList<Term> query, int termID) {
        for (Term term : query) {
            if (term.getTermID() == termID)
                return term.getTermName();
        }
        return null;
    }

    /**
     * @param docID
     * @return true if the doc is already exist in allDocs
     */
    private Document getDoc(int docID) {
        for (Document doc : allDocs) {
            if (doc.getDocID() == docID)
                return doc;
        }
        return null;
    }

    /**
     * This function is used to approach only once to the posting file with different term's id
     *
     * @param queriesFromFile
     * @return HashMap that the key is [TermID/300], the value: ArrayList that contains: TermID;DF
     * the key above represent the posting file name.txt
     */
    private HashMap<Integer, ArrayList<Integer[]>> BuildFromDictionary(ArrayList<Term> queriesFromFile) {
        HashMap<Integer, ArrayList<Integer[]>> result = new HashMap<>();
        for (Term t_Query : queriesFromFile) {
            Integer[] idAndDF = new Integer[2];
            ArrayList<String> dfAndTermID = m_Dicrionary.getOrDefault(t_Query.getTermName(), null);
            if (dfAndTermID != null) {
                int postingName = Integer.parseInt(dfAndTermID.get(1)) / 250;
                idAndDF[0] = Integer.parseInt(dfAndTermID.get(1));//get TermID
                idAndDF[1] = Integer.parseInt(dfAndTermID.get(0));//get DF
                if (result.keySet().contains(postingName)) {
                    ArrayList<Integer[]> idAndDf = result.get(postingName);
                    idAndDf.add(idAndDF);
                } else {
                    ArrayList<Integer[]> idAndDf = new ArrayList<>();
                    idAndDf.add(idAndDF);
                    result.put(postingName, idAndDf);
                }
            }
        }
        for (ArrayList<Integer[]> current : result.values()) {
            current.sort(CompareByTermID);
        }
        return result;
    }

    public ArrayList<Term> getQuery() {
        return query;
    }

    public void setQuery(ArrayList<Term> query) {
        this.query = query;
    }

    public Queue<ArrayList<Document>> getRankedDocs() {
        return rankedDocs;
    }

    public void setAllDocs(ArrayList<Document> allDocs) {
        this.allDocs = allDocs;
    }
}
