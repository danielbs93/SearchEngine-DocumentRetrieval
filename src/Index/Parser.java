package Index;

import Rules.*;
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.*;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class Parser {
    private StringBuilder Doc;
    private ArrayList<Token>[] parserdList;
    private ArrayList<Token> tokenList;
    private ArrayList<Token> afterThisRules;
    private ArrayList<Token> SendingToken;
    private boolean IsStemmerOn;
    private String CorpusPath;
    private static Pattern panctuation = Pattern.compile("[\\p{Punct}&&[^-]&&[^$]&&[^.]]");

    public Parser(String document, String corpusPath, boolean steemer) {
        this.IsStemmerOn = steemer;
        Doc = new StringBuilder(document);
        parserdList = new ArrayList[2];
        for (int i = 0; i < 2; i++) {
            parserdList[i] = new ArrayList<>();
        }
        tokenList = new ArrayList<>();
        afterThisRules = new ArrayList<>();
        SendingToken = new ArrayList<>();
        CorpusPath = corpusPath;
    }


    public ArrayList<Token>[] Parse(){ //MaxentTagger maxentTagger) {
        tokenList = toTokens(this.Doc.toString(),true);
        parseByRules();
        parseByStopWords();
        parseByStemmer();
        parseByUpperLower();
        return parserdList;
    }

    public ArrayList<Token>[] Parse(boolean document) {
        tokenList = toTokens(this.Doc.toString(),document);
        parseByRules();
        parseByStopWords();
        parseByStemmer();
        parseByUpperLower();
        return parserdList;
    }


    /**
     *
     */
    private void parseByUpperLower() {
        UpperLowerCaseParser UpperLower = new UpperLowerCaseParser(tokenList);
        parserdList[1].addAll(UpperLower.Parse());
        tokenList.removeAll(parserdList[1]);
        parserdList[0].addAll(tokenList);
        for (Token token : parserdList[1]) {
            token.setName(token.getName().toUpperCase());
        }
    }

    /**
     *
     */
    private void parseByStemmer() {
        if (!isStemmerOn())
            return;
        Stemmer stemmer = new Stemmer();
        StringBuilder newDoc = new StringBuilder();
        for (Token token : tokenList) {
            stemmer.add(token.getName());
            token.setName(stemmer.stem());
            newDoc.append(token.getName() + " ");
            stemmer.clear();
        }
        Doc = newDoc;
    }


    /**
     *
     */
    private void parseByStopWords() {
        try {
            File file = new File(CorpusPath + "\\StopWords.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bf = new BufferedReader(fileReader);
            LinkedList<String> StopWords = new LinkedList<>();
            String current = "";
            while ((current = bf.readLine()) != null) {
                StopWords.add(current);
            }
            ArrayList<Token> afterStopWords = new ArrayList<>();
            String stopWordUpperCase = "";
            for (Token token : tokenList) {
                if (token.getName().charAt(0) >= 'A' && token.getName().charAt(0) <= 'Z') {
                    stopWordUpperCase = token.getName().toLowerCase();
                } else
                    stopWordUpperCase = token.getName();

                if (!StopWords.contains(stopWordUpperCase)) {
                    afterStopWords.add(token);
                    Doc.append(token.getName() + " ");
                }
            }
            tokenList = afterStopWords;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void parseByRules() {
        Doc = new StringBuilder();
        IParser NumericParser;
        afterThisRules = new ArrayList<>();
        SendingToken = new ArrayList<>();
        for (int i = 0; i < tokenList.size(); i++) {
            boolean addToWords = true;
            //all rules that have to parse only 1 token
            if (tokenList.get(i).getName().contains("%")) {
                SendingToken.add(tokenList.get(i));
                NumericParser = new PercentageParser(SendingToken);
                parserdList[0].add(NumericParser.Parse());
                addToWords = false;
            }
            // 1. w1-w2-w3
            else if (tokenList.get(i).getName().contains("-")) {
                SendingToken.add(tokenList.get(i));
                NumericParser = new RangedParser(SendingToken);
                parserdList[0].add(NumericParser.Parse());
                addToWords = false;
            }
            // 1. Month 2.Day/Year
            else if (DatesParser.isDate(tokenList.get(i).getName()) && isNextIndexAvailable(i) && isInt(tokenList.get(i + 1).getName())) {
                Token DayOrYear = tokenList.get(i + 1);
                if (DayOrYear.isNumeric()) {
                    SendingToken.add(tokenList.get(i));
                    SendingToken.add(tokenList.get(i + 1));
                    NumericParser = new DatesParser(SendingToken);
                    parserdList[0].add(NumericParser.Parse());
                    i++;
                    addToWords = false;
                }
            }
            //1.Between 2.X 3.and 4.Y
            else if (isBetweenRule(i)) {
                for (int j = 0; j < 4; j++)
                    SendingToken.add(tokenList.get(i + j));
                NumericParser = new RangedParser(SendingToken);
                parserdList[0].add(NumericParser.Parse());
                i = i + 3;
                addToWords = false;
            }
            //1. $x
            else if (tokenList.get(i).getName().contains("$")) {
                if (tokenList.get(i).getName().charAt(0) != '$')
                    tokenList.get(i).setName(tokenList.get(i).getName().substring(tokenList.get(i).getName().indexOf("$")));
                if (isNextIndexAvailable(i) && tokenList.get(i + 1).isNumeric()) {
                    tokenList.get(i).setName(tokenList.get(i).getName() + tokenList.get(i + 1).getName());
                    SendingToken.add(tokenList.get(i++));
                } else
                    SendingToken.add(tokenList.get(i));
                //2. quantity unit
                if (isNextIndexAvailable(i + 1) && isQuantityUnit(tokenList.get(i + 1))) {
                    SendingToken.add(tokenList.get(i + 1));
                    i++;
                }
                NumericParser = new PriceParser(SendingToken);
                parserdList[0].add(NumericParser.Parse());
                addToWords = false;
            }
            // 1.Xunit 2.D/dollars
            else if (tokenList.get(i).getName().contains("bn") || tokenList.get(i).getName().contains("m")) {
                String num = tokenList.get(i).getName();
                if (num.contains("bn"))
                    num = num.substring(0, num.indexOf("bn"));
                else if (num.contains("m"))
                    num = num.substring(0, num.indexOf("m"));
                if (StringUtils.isNumeric(num))
                    if (isNextIndexAvailable(i) && isCoin(tokenList.get(i + 1))) {
                        SendingToken.add(tokenList.get(i++));
                        SendingToken.add(tokenList.get(i));
                        NumericParser = new PriceParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                        addToWords = false;
                    }
            }
            //1.X
            else if (tokenList.get(i).isNumeric()) {
                if (isNextIndexAvailable(i)) {
                    //2. Percent/age
                    if (PercentageParser.isPercentage(tokenList.get(i + 1).getName())) {
                        SendingToken.add(tokenList.get(i++));
                        SendingToken.add(tokenList.get(i));
                        NumericParser = new PercentageParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                        addToWords = false;
                    }
                    //2. Month
                    else if (isInt(tokenList.get(i).getName()) && DatesParser.isDate(tokenList.get(i + 1).getName())) {
                        SendingToken.add(tokenList.get(i++));
                        SendingToken.add(tokenList.get(i));
                        NumericParser = new DatesParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                        addToWords = false;
                    }
                    //2. WeightUnit
                    else if (WeightsParser.isWeightUnit(tokenList.get(i + 1).getName())) {
                        SendingToken.add(tokenList.get(i++));
                        SendingToken.add(tokenList.get(i));
                        NumericParser = new WeightsParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                        addToWords = false;
                    }
                    //2. DistanceUnit
                    else if (DistancesParser.isDistanceUnit(tokenList.get(i + 1).getName())) {
                        SendingToken.add(tokenList.get(i++));
                        SendingToken.add(tokenList.get(i));
                        NumericParser = new DistancesParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                        addToWords = false;
                    }
                    //2. Quantity unit
                    else if (isQuantityUnit(tokenList.get(i + 1))) {
                        boolean numberRule = true;
                        if (isNextIndexAvailable(i + 1)) {
                            boolean priceRule = false;
                            //3. D/dollars
                            if (isCoin(tokenList.get(i + 2))) {
                                SendingToken.add(tokenList.get(i));
                                SendingToken.add(tokenList.get(i + 1));
                                SendingToken.add(tokenList.get(i + 2));
                                i += 2;
                                priceRule = true;
                            }
                            //3. U.S/. 4. D/dollars
                            else if (isNextIndexAvailable(i + 2)
                                    && (tokenList.get(i + 2).getName().equals("U.S.") || tokenList.get(i + 2).getName().equals("U.S"))
                                    && isCoin(tokenList.get(i + 3))) {
                                SendingToken.add(tokenList.get(i));
                                SendingToken.add(tokenList.get(i + 1));
                                SendingToken.add(tokenList.get(i + 2));
                                SendingToken.add(tokenList.get(i + 3));
                                i += 3;
                                priceRule = true;
                            }
                            if (priceRule) {
                                numberRule = false;
                                NumericParser = new PriceParser(SendingToken);
                                parserdList[0].add(NumericParser.Parse());
                                addToWords = false;
                            }
                        }// even if a doc is ending with a number rule and there is no continue of words after it.
                        if (numberRule) {
                            SendingToken.add(tokenList.get(i++));
                            SendingToken.add(tokenList.get(i));
                            NumericParser = new NumParser(SendingToken);
                            parserdList[0].add(NumericParser.Parse());
                            addToWords = false;
                        }
                    }
                    //2. fraction
                    else if (tokenList.get(i + 1).isFraction()) {
                        boolean priceRule = false;
                        if (isNextIndexAvailable(i + 1)) {
                            //3. D/dollars
                            if (isCoin(tokenList.get(i + 2))) {
                                SendingToken = AddToSendingToken(i, i + 2);
                                priceRule = true;
                                NumericParser = new PriceParser(SendingToken);
                                parserdList[0].add(NumericParser.Parse());
                                addToWords = false;
                            }
                        }
                        if (!priceRule) {
                            SendingToken = AddToSendingToken(i, i + 1);
                            NumericParser = new NumParser(SendingToken);
                            parserdList[0].add(NumericParser.Parse());
                            addToWords = false;
                        }
                    }
                    //2.D/dollars
                    else if (isCoin(tokenList.get(i + 1))) {
                        SendingToken = AddToSendingToken(i, i + 1);
                        NumericParser = new PriceParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                        addToWords = false;
                    } else {
                        parserdList[0].add((new NumParser(tokenList.get(i))).Parse());
                        addToWords = false;
                    }
                }
                //Its just a number to parse
                else {
                    SendingToken.add(tokenList.get(i));
                    NumericParser = new NumParser(SendingToken);
                    parserdList[0].add(NumericParser.Parse());
                    addToWords = false;
                }
            }
            //Rule of "w1 - w2 - ... - wn" (with spaces)
            else if (isNextIndexAvailable(i) && tokenList.get(i + 1).getName().equals("-")) {
                boolean isEnd = false;
                SendingToken.add(tokenList.get(i));
                boolean isHyphen = true;
                i++;
                while (!isEnd) {
                    if (isNextIndexAvailable(i)) {
                        if (!isHyphen && !tokenList.get(i + 1).getName().equals("-"))
                            isEnd = true;
                        else if (isHyphen)
                            isHyphen = false;
                        else
                            isHyphen = true;
                        SendingToken.add(tokenList.get(i));
                        if (!isEnd)
                            i++;
                    } else {
                        SendingToken.add(tokenList.get(i));
                        isEnd = true;
                    }
                }
                NumericParser = new RangedParser(SendingToken);
                parserdList[0].add(NumericParser.Parse());
                addToWords = false;
            }
            //Token is a word
            if (addToWords){
                afterThisRules.add(tokenList.get(i));
                Doc.append(tokenList.get(i).getName() + " ");
            }
            if (!SendingToken.isEmpty())
                SendingToken.clear();
        }
        tokenList.clear();
        tokenList.addAll(afterThisRules);
    }

    private ArrayList<Token> AddToSendingToken(int start, int end) {
        ArrayList<Token> result = new ArrayList<>();
        for (int i = start; i < end + 1; i++)
            result.add(tokenList.get(i));
        return result;
    }


    /**
     * @param i
     * @return true if its a ranged rule "Between X and Y"
     */
    private boolean isBetweenRule(int i) {
        if (isNextIndexAvailable(i + 1) && isNextIndexAvailable(i + 2) && isNextIndexAvailable(i + 3))
            if ((tokenList.get(i).getName().equals("Between") || tokenList.get(i).getName().equals("between"))
                    && tokenList.get(i + 1).isNumeric()
                    && (tokenList.get(i + 2).getName().equals("and") || tokenList.get(i + 2).equals("And"))
                    && tokenList.get(i + 3).isNumeric())
                return true;
        return false;
    }

    /**
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
     * @return list of token from the text of the doc while removing spaces, dots and commas from the end of a word
     */
    private ArrayList<Token> toTokens(String doc,boolean document) {
        ArrayList<Token> tDoc = new ArrayList<>();
        int position = 0;
        String text = "";
        if(document) {
            int textFinalIndex = doc.length() - 7;
            text = doc.substring(6, textFinalIndex);//Cutting of TEXT labels
        }
        else
            text = doc;
        String[] words = text.split(" ");
        for (String word : words) {
            if (word != null && !word.isEmpty()) {
                Token token = new Token();
                StringBuilder tokenName = new StringBuilder();
                if (!isPanctuationMark(word)) {
                    tokenName.append(word.replaceAll(",", ""));

                    if (tokenName.length() != 0 && !tokenName.equals("")) {
                        String[] splited = panctuation.split(tokenName.toString());
                        for (String name : splited) {
                            if (name.contains(".") || name.contains("-")) {
                                name = StringUtils.stripStart(name, ".");
                                name = StringUtils.stripEnd(name, ".");
                                name = StringUtils.stripStart(name,"-");
                                name = StringUtils.stripEnd(name,"-");
                            }
                            if (!isPanctuationMark(name) && name.length() > 0)
                                tDoc.add(new Token(name, position));
                        }
                    }
                }
                position++;
            }
        }
        return tDoc;
    }

    /**
     * @param word
     * @return
     */
    private boolean LastCharPanctuationMark(String word) {
        if (isPanctuationChar(word.charAt(word.length() - 1)))
            return true;
        return false;
    }

    private boolean FirstCharPanctuationMark(String word) {
        if (isPanctuationChar(word.charAt(0)))
            return true;
        return false;
    }

    /**
     * @param word
     * @return
     */
    private boolean isPanctuationMark(String word) {
        if (word.length() == 1 && (isPanctuationChar(word.charAt(0)) || word.equals("")))
            return true;
        return false;
    }

    private boolean isPanctuationChar(char c) {
        if (c == '(' || c == '{' || c == '[' || c == ')' || c == '}' || c == ']' || c == '-'
                || c == '\'' || c == '.' || c == '*' || c == '|' || c == ':' || c == '?' || c == '\"'
                || c == '<' || c == '>' || c == '!' || c == '`' || c == '/' || c == '~' || c == '+' || c == '#'
                || c == '\\' || c == ';' || c == ',' || c == '_' || c == '=' || c == ' ')
            return true;
        return false;
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

    private boolean isInt(String s) {
        try {
            int i = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException er) {
            return false;
        }
    }
}
