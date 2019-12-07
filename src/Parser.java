import Rules.*;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class Parser {
    //    private HashMap<String, LinkedList<Integer>> Dictionary;
    private String Doc;
    private LinkedList<Token>[] parserdList;
    private LinkedList<Token> tokenList;
    private boolean IsStemmerOn;

    public Parser(String document, boolean steemer) {
        this.IsStemmerOn = steemer;
        Doc = document;
        parserdList = new LinkedList[3];
        tokenList = new LinkedList<>();
    }


    public LinkedList<Token>[] Parser() {
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
        LinkedList<Token> afterThisRules = new LinkedList<>();
        LinkedList<Token> SendingToken = new LinkedList<>();
        IParser NumericParser;
        Iterator<Token> iter = tokenList.iterator();
        for (int i = 0; i < tokenList.size(); i++) {
            //all rules that have to parse only 1 token
            if (tokenList.get(i).getName().contains("%")) {
                NumericParser = new PercentageParser(tokenList.get(i));
                parserdList[0].add(NumericParser.Parse());
            }
            // 1. w1-w2-w3
            else if (((Token) iter).getName().contains("-")) {
                NumericParser = new RangedParser(tokenList.get(i));
                parserdList[0].add(NumericParser.Parse());
            }
            // 1. Month 2.Day/Year
            else if (DatesParser.isDate(((Token) iter).getName()) && isNextIndexAvailable(i)) {
                Token DayOrYear = tokenList.get(i+1);
                if (DayOrYear.isNumeric()) {
                    SendingToken.add(tokenList.get(i));
                    SendingToken.add(tokenList.get(i+1));
                    NumericParser = new DatesParser(SendingToken);
                    parserdList[0].add(NumericParser.Parse());
                    SendingToken.clear();
                    i++;
                }
            }
            //1.Between 2.X 3.and 4.Y
            else if (isBetweenRule(i)) {
                for (int j = 0; j < 4; j++)
                    SendingToken.add(tokenList.get(i+j));
                NumericParser = new RangedParser(SendingToken);
                parserdList[0].add(NumericParser.Parse());
                SendingToken.clear();
                i = i+3;
            }
            //Quotes
            else if (tokenList.get(i).getName().contains("\"")){
                SendingToken.add(tokenList.get(i));
                if (isQuoteRule(i) && isNextIndexAvailable(i)) {
                    i++;
                    while (!tokenList.get(i).getName().contains("\"")){
                        SendingToken.add(tokenList.get(i));
                        i++;
                    }
                    SendingToken.add(tokenList.get(i++));
                    NumericParser = new QuotesParser(SendingToken);
                    parserdList[0].add(NumericParser.Parse());
                    SendingToken.clear();
                }
                // 1.Xunit 2.D/dollars
                else if (tokenList.get(i).getName().contains("bn") || tokenList.get(i).getName().contains("m")) {
                    if (isNextIndexAvailable(i) && isCoin(tokenList.get(i+1))){
                        SendingToken.add(tokenList.get(i));
                        SendingToken.add(tokenList.get(i++));
                        NumericParser = new PriceParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                        SendingToken.clear();
                    }
                }

            }

        }
    }

    private boolean isQuoteRule(int i) {
        if (tokenList.get(i).getName().contains("\"")) {
            String word = tokenList.get(i).getName();
            if (word.lastIndexOf("\"") != word.length()-1)
                return true;
        }
        return false;
    }

    /**
     *
     * @param i
     * @return true if its a ranged rule "Between X and Y"
     */
    private boolean isBetweenRule(int i) {
        if (isNextIndexAvailable(i+1) && isNextIndexAvailable(i+2) && isNextIndexAvailable(i+3))
            if ((tokenList.get(i).getName().equals("Between") || tokenList.get(i).getName().equals("between"))
                &&tokenList.get(i+1).isNumeric()
                && (tokenList.get(i+2).getName().equals("and") || tokenList.get(i+2).equals("And"))
                && tokenList.get(i+3).isNumeric())
                return true;
            return false;
    }

    /**
     *
     * @param i
     * @return true if there is more elements in the tokenList
     */
    private boolean isNextIndexAvailable(int i) {
        if (i + 1 < tokenList.size())
            return true;
        return false;
    }

    /**
     * @param doc
     * @return list of token from the text of the doc
     */
    private LinkedList<Token> toTokens(String doc) {
        LinkedList<Token> tDoc = new LinkedList<>();
        int posision = 0;
        String text = doc.substring(doc.indexOf("<TEXT>") + 1, doc.indexOf("</Text"));
        String[] words = text.split(" ");
        for (String word : words)
            tDoc.add(new Token(word, posision++));
        return tDoc;
    }

    public boolean isStemmerOn() {
        return IsStemmerOn;
    }

    public void setStemmer(boolean stemmer) {
        IsStemmerOn = stemmer;
    }

    private boolean isQuantityUnit(Token unit) {
        String sUnit = unit.getName();
        if (sUnit.equals("million") || sUnit.equals("billion") || sUnit.equals("Million")
                || sUnit.equals("Billion") || sUnit.equals("m") || sUnit.equals("M")
                || sUnit.equals("b") || sUnit.equals("B") || sUnit.equals("bn")
                || sUnit.equals("Thousand") || sUnit.equals("thousand"))
            return true;
        return false;
    }

    private boolean isCoin(Token coin) {
        String sCoin = coin.getName();
        if (sCoin.equals("Dollar") || sCoin.equals("Dollars") || sCoin.equals("dollar")
                || sCoin.equals("dollars"))
            return true;
        return false;
    }
}
