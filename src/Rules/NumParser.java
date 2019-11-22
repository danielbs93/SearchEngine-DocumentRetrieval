package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class NumParser extends Anumbers {
    public NumParser() {
        super();
    }

    public NumParser(LinkedList<Token> tokenList) {
        super(tokenList);
    }

    public NumParser(Token token) {
        super(token);
    }

    @Override
    public LinkedList<Token> Parse() {
        return null;
    }
}
