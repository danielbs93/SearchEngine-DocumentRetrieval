package Rules;


import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 * this class provides the instances of number appearance cases as tokens
 */
public class RulesClassification {
    private Anumbers NumToken;
    private LinkedList<Token> TokenList;

    public static LinkedList<Token> NumberClassification(LinkedList<Token> tokenList) {
        /**
         * token 1.if -> number rule || ranges(num) rule || price rule (first token always be a number, range or price)
         * token 2.if -> nums rule || percents rule || prices rule || dates rule
         * token 3.if -> prices rule
         * token 4.if -> prices rule
         */
        return tokenList;
    }


}
