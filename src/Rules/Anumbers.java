package Rules;
/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */

import java.util.LinkedList;

public abstract class Anumbers implements IParser{
    protected LinkedList<Token> tokenList;

    public Anumbers() {
        tokenList = new LinkedList<>();
    }
    public Anumbers(LinkedList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public  Anumbers(Token token) {
        tokenList = new LinkedList<>();
        tokenList.add(token);
    }

    //COMPLETE -> FACTORY PATTERN
    public static IParser ClassifyRule() {

        return null;
    }
}
