package RetrieveDocuments.AtomicClasses;
import RetrieveDocuments.AtomicClasses.Term;
import org.apache.commons.lang3.StringUtils;

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
    private double finalRank;
    private ArrayList<Term> myTerms;
    private ArrayList<Term> dominantEntities;

    //data form: docNO;fileNO;maxTF;uniqueWords;docLength
    public Document(ArrayList<String> data, int id) {
        docNO = data.get(0);
        fileNO = data.get(1);
        maxTfTerm = Integer.parseInt(data.get(2));
        uniqueWords = Integer.parseInt(data.get(3));
        docLength = Integer.parseInt(data.get(4));
        docID = id;
        rank = new double[3];
        for (int i = 0; i < 3; i++) {
            rank[i] = 0;
        }
        myTerms = new ArrayList<>();
        dominantEntities = new ArrayList<>();
        finalRank = 0;
    }

    public String getDocNO() {
        String name = StringUtils.stripStart(docNO," ");
        name = StringUtils.stripStart(name,"\t");
        return name;
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

    public Term getTermByName(Term t) {
        for (Term term: myTerms) {
            if (term.equals(t))
                return term;
        }
        return null;
    }

    public boolean isTermExist(Term term) {
        if (myTerms.contains(term))
            return true;
        return false;
    }

    public ArrayList<Term> getDominantEntities() {
        return this.dominantEntities;
    }

    public String getEntitiesToString(){
        String entities = "";
        for (int i = 0; i < dominantEntities.size() && i < 5 ; i++) {
            entities += dominantEntities.get(i).getTermName() + " ";
        }
        return entities;
    }

    public void setDominantEntities(ArrayList<Term> entities) {
        if (entities != null)
            this.dominantEntities = entities;
    }

    public String getFileNO() {
        return fileNO;
    }

    public double getFinalRank() {
        return finalRank;
    }

    public void setFinalRank(double finalRank) {
        this.finalRank = finalRank;
    }
}
