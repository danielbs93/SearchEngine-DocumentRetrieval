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

    /**
     * insert the queries to HashMap
     */
    public void ReadQuery(){
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
                        ArrayList<Token>[] tokens = parser.Parse();
                        ArrayList<Term> terms = ConvertTokensTOTerms(tokens);
                        Comparator<Term> comparatorByPosition = Comparator.comparingInt(o->o.getPositions()[0]);
                        terms.sort(comparatorByPosition);
                        queries.put(queryNum , new MutablePair<>(terms,null));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            parser = new Parser(this.query,this.corpusPath,this.isStemmer);
            ArrayList<Token>[] tokens = parser.Parse();
            ArrayList<Term> terms = ConvertTokensTOTerms(tokens);
            Comparator<Term> comparatorByPosition = Comparator.comparingInt(o->o.getPositions()[0]);
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
                int termId = Integer.parseInt(fromDictionary.get(1));
                Term term = new Term(token.getName(),termId);
                term.setQueryTermPositions(token.getPosition());
                toTerms.add(term);
            }
        }
        for (Token token:tokens[1]) {
            ArrayList<String> fromDictionary = dictionary.getOrDefault(token.getName(),null);
            if(fromDictionary == null)
                fromDictionary = dictionary.getOrDefault(token.getName().toLowerCase(),null);
            if(fromDictionary != null){
                int termId = Integer.parseInt(fromDictionary.get(1));
                Term term = new Term(token.getName(),termId);
                term.setQueryTermPositions(token.getPosition());
                toTerms.add(term);
            }
        }
        return toTerms;
    }

    public void Retriev(){
        mySearcher = new Searcher(this.postingPath,this.isSemantic);
        sortedQueriesId = (List<Integer>) queries.keySet();
        sortedQueriesId.sort(Integer::compareTo);
        ArrayList<ArrayList<Term>> queriesToRank = new ArrayList<>();
        for (int i = 0; i <sortedQueriesId.size() ; i++)
            queriesToRank.add(queries.get(sortedQueriesId.get(i)).getLeft());
        mySearcher.Search(queriesToRank);
        Queue<ArrayList<Document>> retrievDocuments = mySearcher.Rank();
        for (int i = 0; i <sortedQueriesId.size() ; i++)
            queries.get(sortedQueriesId.get(i)).setRight(retrievDocuments.poll());

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
                 stringBuilder.append(queryId + " 7 " + document.getDocNO() + " 7 7 7");

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

}
