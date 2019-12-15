package Rules;
/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class Anumbers implements IParser{
    protected ArrayList<Token> tokenList;
    protected Token Result;
    protected DecimalFormat df3 = new DecimalFormat("#.###");


    public Anumbers() {
        Result = new Token();
        tokenList = new ArrayList<>();
        df3.setRoundingMode(RoundingMode.DOWN);
    }
    public Anumbers(ArrayList<Token> tokenList) {
        Result = new Token();
        this.tokenList = tokenList;
        df3.setRoundingMode(RoundingMode.DOWN);
    }

    public  Anumbers(Token token) {
        Result = new Token(token.getName());
        tokenList = new ArrayList<>();
        tokenList.add(token);
        df3.setRoundingMode(RoundingMode.DOWN);
    }

    public Anumbers(String[] s_array) {
        Result = new Token();
        tokenList = new ArrayList<>();
        df3.setRoundingMode(RoundingMode.DOWN);
        for (String s: s_array) {
            tokenList.add(new Token(s));
        }
    }

    /**
     * After taking the token list from functions above as a parameter, unite them into 1 token
     * @param withSpace
     */
    protected void Unite( boolean withSpace) {
        for (Token t: tokenList) {
            if (withSpace)
                Result.setName(Result.getName() + " " + t.getName());
            else
                Result.setName(Result.getName() + t.getName());
        }
    }

}
