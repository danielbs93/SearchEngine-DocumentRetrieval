package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class UpperLowerCaseParser extends Atext {

    public UpperLowerCaseParser() {
        super();
    }

    public UpperLowerCaseParser(String document) {
        super(document);
    }

    public UpperLowerCaseParser(String[] s_Array) {
        super(s_Array);
    }

    @Override
    public LinkedList<Token> Parse() {
        for (String current:s_Array) {
            if (!current.isEmpty() && FirstCharIsUpper(current)) {
                if (current.contains(","))
                    current = current.replaceAll(",","");
                if (current.contains(".") && !IsUpper(current.charAt(current.length() - 2)))
                    current = current.substring(0,current.length() - 1);
                if (current.contains("'s"))
                    current = current.replaceAll("'s","");
                tokenList.addLast(new Token(current));
            }
        }
        removeDuplicates();
        return tokenList;
    }

    private boolean FirstCharIsUpper(String current) {
        if (current.charAt(0) >= 'A' && current.charAt(0) <= 'Z')
            return true;
        return false;
    }

    private boolean IsUpper(char c) {
        if (c >= 'A' && c <= 'Z')
            return true;
        return false;
    }
}
