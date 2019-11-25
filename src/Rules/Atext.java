package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public abstract class Atext implements IParser {
    protected String[] s_Array;
    protected LinkedList<Token> tokenList;

    public Atext(String[] s_Array) {
        this.s_Array = s_Array;
        tokenList = new LinkedList<>();
    }
}
