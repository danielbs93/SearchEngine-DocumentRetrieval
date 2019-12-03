import Rules.Token;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class Parser {
    private HashMap<String, LinkedList<Integer>> Dictionary;
    private String Doc;
    private LinkedList<String> parserdList;
    private LinkedList<Token> tokenList;
    private boolean IsStemmerOn;

    public Parser(String document,boolean steemer)
    {
        this.IsStemmerOn = steemer;
        Doc = document;
    }


    public LinkedList<String> Parser() {
        tokenList = toTokens(this.Doc);
        parseByRules();
        parseByStopWords();
        parseByEntities();
        parseBySteemer();
        parseByUpperLower();
        return parserdList;
    }

    /**
     *
     */
    private void parseByUpperLower() {
    }

    /**
     *
     */
    private void parseBySteemer() {
        if (!isStemmerOn())
            return;

    }

    /**
     *
     */
    private void parseByEntities() {
    }

    /**
     *
     */
    private void parseByStopWords() {
    }

    /**
     *
     */
    private void parseByRules() {


    }

    /**
     *
     * @param doc
     * @return list of token from the text of the doc
     */
    private LinkedList<Token> toTokens (String doc){
        LinkedList<Token> tDoc = new LinkedList<>();
        int posision = 0;
        String text = doc.substring(doc.indexOf("<TEXT>")+1,doc.indexOf("</Text"));
        String[] words = text.split(" ");
        for (String word:words)
            tDoc.add(new Token(word,posision++));
        return tDoc;
    }

    public boolean isStemmerOn() {
        return IsStemmerOn;
    }

    public void setStemmer(boolean stemmer) {
        IsStemmerOn = stemmer;
    }

    private boolean isQuantityUnit(Token unit){
        String sUnit = unit.getName();
        if (sUnit.equals("million")||sUnit.equals("billion")||sUnit.equals("Million")
            ||sUnit.equals("Billion")||sUnit.equals("m")||sUnit.equals("M")
            ||sUnit.equals("b")||sUnit.equals("B")||sUnit.equals("bn")
            ||sUnit.equals("Thousand")||sUnit.equals("thousand"))
            return true;
        return false;
    }

    private boolean isCoin(Token coin){
        String sCoin = coin.getName();
        if(sCoin.equals("Dollar")||sCoin.equals("Dollars")||sCoin.equals("dollar")
            ||sCoin.equals("dollars"))
            return true;
        return false;
    }
}
