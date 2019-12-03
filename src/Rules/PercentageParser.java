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
    public Token Parse() {
        NumParser p;
        Token num;
        //The form is X% , example: "5.123465%" than we need to separate and NumParser 5.123465
        if (tokenList.size() == 1) {
            Token original = tokenList.remove();
            //taking the number part
            num = new Token(original.getName().substring(0,original.getName().indexOf('%')));
            p = new NumParser(num);
            Result = p.Parse();
            //adding "%" to the token after we parsed it
            Result.setName(Result.getName() + "%");
            return Result;
        }
        //The form is "X percent/percentage" -> saving as "X%"
        else if (tokenList.size() == 2) {
            num = new Token(tokenList.removeFirst().getName());
            p = new NumParser(num);
            Result = p.Parse();
            Result.setName(Result.getName() + "%");
            return Result;
        }
        else
            return null;
    }

    public static boolean isPercentage(String word){
        if (word.equals("percentage")|| word.equals("percentages")||word.equals("Percentage")
        ||word.equals("Percentages")||word.equals("percent")||word.equals("Percent")||word.equals("%"))
            return true;
        return false;
    }
}
