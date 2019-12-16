package Rules;

import java.util.ArrayList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public abstract class Atext {
    protected String[] s_Array;
    protected ArrayList<Token> tokenList;
    protected StringBuilder Document;

    public Atext() {
        tokenList = new ArrayList<>();
        Document = new StringBuilder();
    }

    public Atext(String document) {
        Document = new StringBuilder();
        Document.append(document);
        tokenList = new ArrayList<>();
        s_Array = document.split(" ");
    }

    public Atext(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        Document = new StringBuilder();
        for (Token token:tokenList) {
            Document.append(token.getName() + " ");
        }
    }

    public Atext(String[] s_Array) {
        Document = new StringBuilder();
        this.s_Array = s_Array;
        tokenList = new ArrayList<>();
        for (String s : s_Array) {
            Document.append(s + " ");
        }
    }

    protected void removeDuplicates(ArrayList<Token> tokenlist) {
        for (int i = 0; i < tokenlist.size(); i++) {
            for (int j = i + 1; j < tokenlist.size(); j++) {
                if (tokenlist.get(i).equals(tokenlist.get(j)))
                    tokenlist.remove(j);
            }
        }
    }
}

