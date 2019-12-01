package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public abstract class Atext {
    protected String[] s_Array;
    protected LinkedList<Token> tokenList;
    protected String Document;

    public Atext() {
        tokenList = new LinkedList<>();
    }

    public Atext(String document) {
        Document = document;
        tokenList = new LinkedList<>();
        s_Array = document.split(" ");
    }

    public Atext(String[] s_Array) {
        this.s_Array = s_Array;
        tokenList = new LinkedList<>();
        for (String s : s_Array) {
            Document = Document + s + " ";
        }
        Document = Document.substring(0, Document.length() - 1);
    }

    public LinkedList<Token> Parse() {
        return null;
    }


    protected void removeDuplicates() {
        for (int i = 0; i < tokenList.size(); i++) {
            for (int j = i + 1; j < tokenList.size(); j++) {
                if (tokenList.get(i).equals(tokenList.get(j)))
                    tokenList.remove(j);
            }
        }
    }
}
