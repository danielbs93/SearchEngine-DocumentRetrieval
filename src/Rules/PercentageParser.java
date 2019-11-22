package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class PercentageParser extends Anumbers {

    public PercentageParser() {
        super();
    }

    public PercentageParser(LinkedList<Token> tokenList) {
        super(tokenList);
    }

    public PercentageParser(Token token) {
        super(token);
    }

    @Override
    public LinkedList<Token> Parse() {
        NumParser p;
        Token num;
        //The form is X% , example: "5.123465%" than we need to separate and NumParser 5.123465
        if (tokenList.size() == 1) {
            Token original = tokenList.remove();
            //taking the number part
            num = new Token(original.getName().substring(0,original.getName().indexOf("%")));
            p = new NumParser(num);
            tokenList = p.Parse();
            //adding "%" to the token after we parsed it
            tokenList.getFirst().setName(tokenList.getFirst().getName() + "%");
            return tokenList;
        }
        //The form is "X percent/percentage" -> saving as "X%"
        else if (tokenList.size() == 2) {
            num = new Token(tokenList.removeFirst().getName());
            p = new NumParser(num);
            tokenList = p.Parse();
            tokenList.getFirst().setName(tokenList.getFirst().getName() + "%");
            return tokenList;
        }
        else
            return null;
    }
}
