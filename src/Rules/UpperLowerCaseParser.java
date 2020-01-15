package Rules;

import java.util.ArrayList;

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

    public UpperLowerCaseParser(ArrayList<Token> tList) {
        s_Array = new String[tList.size()];
        int i = 0;
        for (Token token: tList) {
            s_Array[i] = token.getName() + "#" + token.getPosition();
            i++;
        }
    }


    public ArrayList<Token> Parse() {
        for (String current:s_Array) {
            if (!current.isEmpty() && FirstCharIsUpper(current)) {
                if (current.contains("'s"))
                    current = current.replaceAll("'s", "");
                String pos = current.substring(current.indexOf("#") + 1);
                try {
                    int position = Integer.parseInt(pos);
                    tokenList.add(new Token(current.substring(0, current.indexOf("#")), position,true));
                } catch(NumberFormatException e){
                    
                }
            }
        }
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
