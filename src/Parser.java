import Rules.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Daniel Ben-Simon & Eran Toutian
 */
public class Parser {
    //    private HashMap<String, LinkedList<Integer>> Dictionary;
    private String Doc;
    private ArrayList<Token>[] parserdList;
    private ArrayList<Token> tokenList;
//    private LinkedList<Token> tokenList;
    private boolean IsStemmerOn;

    public Parser(String document, boolean steemer) {
        this.IsStemmerOn = steemer;
        Doc = document;
        parserdList = new ArrayList[2];
        for (int i = 0; i < 2; i++) {
            parserdList[i] = new ArrayList<>();
        }
        tokenList = new ArrayList<>();
    }


    public ArrayList<Token>[] Parse(MaxentTagger maxentTagger) {
        tokenList = toTokens(this.Doc);
        parseByRules();
        parseByStopWords();
        parseByStemmer();
        parseByEntities(maxentTagger);
        parseByUpperLower();
        return parserdList;
    }


    /**
     *
     */
    private void parseByUpperLower() {
        Atext UpperLower = new UpperLowerCaseParser(tokenList);
        parserdList[1].addAll(UpperLower.Parse());
        tokenList.removeAll(parserdList[1]);
        parserdList[0].addAll(tokenList);
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
        Doc = newDoc.toString();
    }

    /**
     *
     */
    private void parseByEntities(MaxentTagger maxentTagger) {
//        String[] tokenlist = (String[]) tokenList.toArray();
        String[] tokenlist = Doc.split(" ");
        Atext es = new EntitiesParser(tokenlist, maxentTagger);
        parserdList[1] = es.Parse();
        tokenList.removeAll(parserdList[1]);
        Doc = ((EntitiesParser) es).getDocAsString();
    }

    /**
     *
     */
    private void parseByStopWords() {
        try {
            File file = new File(getClass().getResource("StopWords.txt").toURI());
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
                if (token.getName().charAt(0) >= 'A' || token.getName().charAt(0) <= 'Z') {
                    stopWordUpperCase = token.getName().toLowerCase();
                } else
                    stopWordUpperCase = token.getName();

                if (!StopWords.contains(stopWordUpperCase)) {
                    afterStopWords.add(token);
                    Doc += token.getName() + " ";
                }
            }
            tokenList = afterStopWords;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void parseByRules() {
        Doc = "";
        ArrayList<Token> afterThisRules = new ArrayList<>();
        ArrayList<Token> SendingToken = new ArrayList<>();
        IParser NumericParser;
        for (int i = 0; i < tokenList.size(); i++) {
            //all rules that have to parse only 1 token
            if (tokenList.get(i).getName().contains("%")) {
                NumericParser = new PercentageParser(tokenList.get(i));
                parserdList[0].add(NumericParser.Parse());
            }
            // 1. w1-w2-w3
            else if (tokenList.get(i).getName().contains("-")) {
                NumericParser = new RangedParser(tokenList.get(i));
                parserdList[0].add(NumericParser.Parse());
            }
            // 1. Month 2.Day/Year
            else if (DatesParser.isDate(tokenList.get(i).getName()) && isNextIndexAvailable(i) && isInt(tokenList.get(i+1).getName())) {
                Token DayOrYear = tokenList.get(i + 1);
                if (DayOrYear.isNumeric()) {
                    SendingToken.add(tokenList.get(i));
                    SendingToken.add(tokenList.get(i + 1));
                    NumericParser = new DatesParser(SendingToken);
                    parserdList[0].add(NumericParser.Parse());
                    i++;
                }
            }
            //1.Between 2.X 3.and 4.Y
            else if (isBetweenRule(i)) {
                for (int j = 0; j < 4; j++)
                    SendingToken.add(tokenList.get(i + j));
                NumericParser = new RangedParser(SendingToken);
                parserdList[0].add(NumericParser.Parse());
                i = i + 3;
            }
            //Quotes
            else if (tokenList.get(i).getName().charAt(0) == '"') {
                int ifItJustQuotationMark = i;//if its start with quotation mark and does not end with one
                boolean QuotationMarkOnlyInStart = false;
                SendingToken.add(tokenList.get(i));
                if (isQuoteRule(i) && isNextIndexAvailable(i)) {
                    i++;
                    while (i < tokenList.size() && !tokenList.get(i).getName().contains("\"")) {
                        SendingToken.add(tokenList.get(i));
                        i++;
                    }
                    if (i == tokenList.size() && !tokenList.get(i - 1).getName().contains("\""))
                        QuotationMarkOnlyInStart = true;
                    else {
                        int length = tokenList.get(i).getName().length() - 2;
                        if (tokenList.get(i).getName().length() > 1 && tokenList.get(i).getName().charAt(length) == '.')
                            tokenList.get(i).setName(tokenList.get(i).getName().substring(0, length) + "\"");
                        if (tokenList.get(i).getName().length() > 1 && tokenList.get(i).getName().charAt(length) == ',')
                            tokenList.get(i).setName(tokenList.get(i).getName().substring(0, length) + "\"");
                        SendingToken.add(tokenList.get(i));
                    }
                }
                if (QuotationMarkOnlyInStart) {
                    i = ifItJustQuotationMark - 1;
                    String word = tokenList.get(ifItJustQuotationMark).getName().substring(1);
                    tokenList.get(ifItJustQuotationMark).setName(word);//without quotation mark
                } else {
                    NumericParser = new QuotesParser(SendingToken);
                    parserdList[0].add(NumericParser.Parse());
                }
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
                    }
                    //2. Month
                    else if (isInt(tokenList.get(i).getName()) && DatesParser.isDate(tokenList.get(i + 1).getName())) {
                        SendingToken.add(tokenList.get(i++));
                        SendingToken.add(tokenList.get(i));
                        NumericParser = new DatesParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                    }
                    //2. WeightUnit
                    else if (WeightsParser.isWeightUnit(tokenList.get(i + 1).getName())) {
                        SendingToken.add(tokenList.get(i++));
                        SendingToken.add(tokenList.get(i));
                        NumericParser = new WeightsParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
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
                            }
                        }// even if a doc is ending with a number rule and there is no continue of words after it.
                        if (numberRule) {
                            SendingToken.add(tokenList.get(i++));
                            SendingToken.add(tokenList.get(i));
                            NumericParser = new NumParser(SendingToken);
                            parserdList[0].add(NumericParser.Parse());
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
                            }
                        }
                        if (!priceRule) {
                            SendingToken = AddToSendingToken(i, i + 1);
                            NumericParser = new NumParser(SendingToken);
                            parserdList[0].add(NumericParser.Parse());
                        }
                    }
                    //2.D/dollars
                    else if (isCoin(tokenList.get(i + 1))) {
                        SendingToken = AddToSendingToken(i, i + 1);
                        NumericParser = new PriceParser(SendingToken);
                        parserdList[0].add(NumericParser.Parse());
                    } else
                        parserdList[0].add((new NumParser(tokenList.get(i))).Parse());
                }
                //Its just a number to parse
                else {
                    NumericParser = new NumParser(tokenList.get(i));
                    parserdList[0].add(NumericParser.Parse());
                }
            }
            //1. $x
            else if (tokenList.get(i).getName().contains("$")){
                if (tokenList.get(i).getName().charAt(0) != '$')
                    tokenList.get(i).setName(tokenList.get(i).getName().substring(tokenList.get(i).getName().indexOf("$")));
                if (isNextIndexAvailable(i) && tokenList.get(i+1).isNumeric()) {
                    tokenList.get(i).setName(tokenList.get(i).getName() + tokenList.get(i+1).getName());
                    SendingToken.add(tokenList.get(i++));
                }
                else
                    SendingToken.add(tokenList.get(i));
                //2. quantity unit
                if (isNextIndexAvailable(i+1) && isQuantityUnit(tokenList.get(i+1))){
                    SendingToken.add(tokenList.get(i+1));
                    i++;
                }
                NumericParser = new PriceParser(SendingToken);
                parserdList[0].add(NumericParser.Parse());
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
            }
            //Token is a word
            else {
                afterThisRules.add(tokenList.get(i));
                Doc += tokenList.get(i).getName() + " ";
            }
            if (!SendingToken.isEmpty())
                SendingToken.clear();
        }
        tokenList = afterThisRules;
    }

    private ArrayList<Token> AddToSendingToken(int start, int end) {
        ArrayList<Token> result = new ArrayList<>();
        for (int i = start; i < end + 1; i++)
            result.add(tokenList.get(i));
        return result;
    }

    /**
     * @param i
     * @return true if it is a quote rule of the form "w1 w2 w3... wn"
     */
    private boolean isQuoteRule(int i) {
        if (tokenList.get(i).getName().contains("\"")) {
            String word = tokenList.get(i).getName();
            if (word.lastIndexOf("\"") != word.length() - 1)
                return true;
        }
        return false;
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
    private ArrayList<Token> toTokens(String doc) {
        ArrayList<Token> tDoc = new ArrayList<>();
        int position = 0;
        int textFinalIndex = doc.length() - 7;
        String text = doc.substring(6, textFinalIndex);//Cutting of TEXT labels
        String[] words = text.split(" ");
        for (String word : words) {
            if (word != null && !word.isEmpty()) {
                Token token = new Token();
                StringBuilder tokenName = new StringBuilder();
                if (!isPanctuationMark(word)) {
                    tokenName.append(word);
                    while (tokenName.length() > 0 && FirstCharPanctuationMark(word)) {
                        tokenName.deleteCharAt(0);
                    }
                    while (tokenName.length() != 0 && LastCharPanctuationMark(word)) {
                        int length = tokenName.length()-1;
                        tokenName.deleteCharAt(length);
                    }
                    if (tokenName.length() != 0)
                        tDoc.add(new Token(tokenName.toString(), position++));
                }
            }
        }
        return tDoc;
    }

    /**
     * @param word
     * @return
     */
    private boolean LastCharPanctuationMark(String word) {
        if (word.lastIndexOf(".") == word.length() - 1 || word.lastIndexOf(",") == word.length() - 1
                || word.lastIndexOf(")") == word.length() - 1 || word.lastIndexOf("}") == word.length() - 1
                || word.lastIndexOf("'") == word.length() - 1 || word.lastIndexOf("]") == word.length() - 1
                || word.lastIndexOf("*") == word.length() - 1 || word.lastIndexOf(":") == word.length() - 1
                || word.lastIndexOf(";") == word.length() - 1 || word.lastIndexOf("|") == word.length() - 1)
            return true;
        return false;
    }

    private boolean FirstCharPanctuationMark(String word) {
        if (word.indexOf("(") == 0 || word.indexOf("{") == 0 || word.indexOf("[") == 0
                || word.indexOf("'") == 0 || word.indexOf(".") == 0 || word.indexOf("*") == 0 || word.indexOf("|") == 0)
            return true;
        return false;
    }

    /**
     * @param word
     * @return
     */
    private boolean isPanctuationMark(String word) {
        if (word.equals(".") || word.equals(",") || word.equals("(") || word.equals(" ")
                || word.equals("}") || word.equals("{") || word.equals(")")
                || word.equals("[") || word.equals("]") || word.equals("'")
                || word.equals(":") || word.equals(";") || word.equals("%"))
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

    private boolean isInt(String s)
    {
        try
        { int i = Integer.parseInt(s); return true; }

        catch(NumberFormatException er)
        { return false; }
    }
}
