package RetrieveDocuments;

import Index.Parser;
import RetrieveDocuments.AtomicClasses.Document;
import RetrieveDocuments.AtomicClasses.Term;
import Rules.Token;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

public class RetrieveManager {

    private Searcher mySearcher;
    private Parser parser;
    private boolean isStemmer;
    private boolean isSemantic;
    private boolean isDominantEntities;
    private boolean isPath;
    private String query;
    private String corpusPath;
    private String postingPath;
    private Integer queryIdCounter = 900;
    private IdentifyDominantEntities dominantEntities;
    private  List<Integer> sortedQueriesId;
    private HashMap<Integer, MutablePair<ArrayList<Term>,ArrayList<Document>>> queries; // <queryID,<query, RankedDoc>>
    private HashMap<String, ArrayList<String>> dictionary;

    public RetrieveManager(boolean isStemmer, boolean isSemantic, boolean dominantEntities, boolean isPath
            , String query, String corpusPath, String postingPath, HashMap<String, ArrayList<String>> dictionary) {
        this.isStemmer = isStemmer;
        this.isSemantic = isSemantic;
        this.query = query;
        this.isPath = isPath;
        this.isDominantEntities = dominantEntities;
        this.corpusPath = corpusPath;
        this.postingPath = postingPath;
        this.dictionary = dictionary;
        queries = new HashMap<>();
    }

    public boolean Start() {
        ReadQuery();
        if (Retrieve()){
            if (isDominantEntities)
                FindDominantEntities();
            SaveRetrievalInformation();
            return true;
        }
        return false;
    }

    /**
     * insert the queries to HashMap
     */
    public void ReadQuery(){
        Comparator<Term> comparatorByPosition = Comparator.comparingInt(o->o.getPositions()[0]);
        if(this.isPath){
            try {
                File file = new File(this.query);
                List<String> list = Files.readAllLines(file.toPath());
                int queryNum = 0;
                for (String line:list) {
                    if(line.contains("<num>")){
                        String [] splitLine = line.split(" ");
                        queryNum = Integer.parseInt(splitLine[2]);
                    }
                    if(line.contains("<title>")){
                        String queryToParse = line.substring(7);
                        parser = new Parser(queryToParse,corpusPath,this.isStemmer);
                        ArrayList<Token>[] tokens = parser.Parse(false);
                        ArrayList<Term> terms = ConvertTokensTOTerms(tokens);
                        terms.sort(comparatorByPosition);
                        queries.put(queryNum , new MutablePair<>(terms,null));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //single query
        else {
            parser = new Parser(this.query,this.corpusPath,this.isStemmer);
            ArrayList<Token>[] tokens = parser.Parse(false);
            ArrayList<Term> terms = ConvertTokensTOTerms(tokens);
            terms.sort(comparatorByPosition);
            queries.put(queryIdCounter++ , new MutablePair<>(terms,null));
        }
    }

    /**
     * @param tokens to convert
     * @return list of terms
     */
    private ArrayList<Term> ConvertTokensTOTerms(ArrayList<Token>[] tokens){
        ArrayList<Term> toTerms = new ArrayList<>();
        for(Token token:tokens[0]) {
            ArrayList<String> fromDictionary = dictionary.getOrDefault(token.getName(),null);
            if(fromDictionary != null){
                AddToTermQueryList(token,toTerms,fromDictionary);
            }
        }
        for (Token token:tokens[1]) {
            ArrayList<String> fromDictionary = dictionary.getOrDefault(token.getName(),null);
            if(fromDictionary == null)
                fromDictionary = dictionary.getOrDefault(token.getName().toLowerCase(),null);
            if(fromDictionary != null){
                AddToTermQueryList(token,toTerms,fromDictionary);
            }
        }
        return toTerms;
    }

    private void AddToTermQueryList(Token token, ArrayList<Term> toTerms, ArrayList<String> fromDictionary) {
        int termId = Integer.parseInt(fromDictionary.get(1));
        Term term = new Term(token.getName(),termId);
        term.setDf(Integer.parseInt(fromDictionary.get(0)));
        term.setQueryTermPositions(token.getPosition());
        toTerms.add(term);
    }

    public boolean Retrieve(){
        mySearcher = new Searcher(this.postingPath,this.isSemantic,this.dictionary);
        sortedQueriesId = new ArrayList<> (queries.keySet());
        sortedQueriesId.sort(Integer::compareTo);
        ArrayList<ArrayList<Term>> queriesToRank = new ArrayList<>();
        for (int i = 0; i <sortedQueriesId.size() ; i++)
            queriesToRank.add(queries.get(sortedQueriesId.get(i)).getLeft());
        mySearcher.Search(queriesToRank);
        Queue<ArrayList<Document>> retrievDocuments = mySearcher.getRankedDocs();
        if(retrievDocuments.size()==0)
            return false;
        for (int i = 0; i <sortedQueriesId.size() ; i++)
            queries.get(sortedQueriesId.get(i)).setRight(retrievDocuments.poll());
        return true;
    }

    /**
     * Save the retrieval information: queryId Iter=7 docNO Rank=7 Sim=7 Run_ID=7
     */
    public void SaveRetrievalInformation(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <sortedQueriesId.size() ; i++) {
            int queryId = sortedQueriesId.get(i).intValue();
            ArrayList<Document> documents = this.queries.get(queryId).getRight();
            for (Document document:documents)
                if(!isDominantEntities)
                 stringBuilder.append(queryId + " 0 " + document.getDocNO() + " 7 7 7\n");
                else
                    stringBuilder.append(queryId + " 0 " + document.getDocNO() + " 7 7 7 || Entities:  " + document.getEntitiesToString() + "\n");

        }
        File file = new File(this.postingPath + "\\RetrievalDocuments.txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(stringBuilder.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This function responsible for retrieving dominant entities in the given doc
     * dominant entities retrieved by their tf in the document
     */
    public void FindDominantEntities() {
        dominantEntities = new IdentifyDominantEntities(this.corpusPath,this.isStemmer);
        for (Integer key: this.sortedQueriesId) {
            ArrayList<Document> currentRanked = this.queries.get(key).getRight();
            for (Document currentDoc: currentRanked) {
                if (currentDoc.getDominantEntities().size() <= 0) {
                    dominantEntities.setDocument(currentDoc);
                    currentDoc.setDominantEntities(dominantEntities.get5DominantEntities(this.dictionary));
                }
            }
        }
    }

}
