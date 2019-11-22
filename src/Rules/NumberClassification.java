package Rules;


import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 * this class provides the instances of number appearance cases as tokens
 */
public class NumberClassification {
    private Anumbers NumToken;
    private LinkedList<Token> TokenList;

    public static LinkedList<Token> NumberClassification(LinkedList<Token> tokenList) {
        /**
         * token 1.if -> number rule || ranges(num) rule (first token always be a number or range)
         * token 2.if -> nums rule || percents rule || prices rule || dates rule
         * token 3.if -> prices rule
         * token 4.if -> prices rule
         */
        return tokenList;
    }


}
