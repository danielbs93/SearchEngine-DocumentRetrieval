package RetrieveDocuments.AtomicClasses;
import RetrieveDocuments.AtomicClasses.Term;

import java.util.ArrayList;

public class Document {

    public enum RankType  {
            TFIDF,Semantic,BM25
    }
    private String docNO;
    private String fileNO;
    private int docID;
    private int docLength;
    private int maxTfTerm;
    private int uniqueWords;
    private double[] rank;
    private ArrayList<Term> myTerms;

    public Document(ArrayList<String> data, int id) {
        docNO = data.get(0);
        fileNO = data.get(1);
        maxTfTerm = Integer.parseInt(data.get(2));
        uniqueWords = Integer.parseInt(data.get(3));
        docLength = Integer.parseInt(data.get(4));
        docID = id;
        rank = new double[3];
        for (int i = 0; i < 3; i++) {
            rank[i] = -1;
        }
        myTerms = new ArrayList<>();
    }

    public String getDocNO() {
        return docNO;
    }

    public void setDocNO(String docNO) {
        this.docNO = docNO;
    }

    public int getDocLength() {
        return docLength;
    }

    public void setDocLength(int docLength) {
        this.docLength = docLength;
    }

    public int getMaxTfTerm() {
        return maxTfTerm;
    }

    public void setMaxTfTerm(int maxTfTerm) {
        this.maxTfTerm = maxTfTerm;
    }

    public int getUniqueWords() {
        return uniqueWords;
    }

    public void setUniqueWords(int uniqueWords) {
        this.uniqueWords = uniqueWords;
    }

    public double getRank(RankType type) {
        if (type == RankType.TFIDF)
            return rank[0];
        else if (type == RankType.BM25)
            return rank[1];
        else//Semantic
            return rank[2];
    }

    public void setRank(RankType type, double rankk) {
        if (type == RankType.TFIDF)
            rank[0] = rankk;
        else if (type == RankType.BM25)
            rank[1] = rankk;
        else//Semantic
            rank[2] = rankk;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public void addTerm(Term term) {
        myTerms.add(term);
    }

    public ArrayList<Term> getDocTerms() {
        return myTerms;
    }

    public Term getTerm(Term term) {
        return myTerms.get(myTerms.indexOf(term));
    }

    public boolean isTermExist(Term term) {
        if (myTerms.contains(term))
            return true;
        return false;
    }
}