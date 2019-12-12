package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public abstract class Atext {
    protected String[] s_Array;
    protected LinkedList<Token> tokenList;
    protected StringBuilder Document;

    public Atext() {
        tokenList = new LinkedList<>();
        Document = new StringBuilder();
    }

    public Atext(String document) {
        Document = new StringBuilder();
        Document.append(document);
        tokenList = new LinkedList<>();
        s_Array = document.split(" ");
    }

    public Atext(LinkedList<Token> tokenList) {
        this.tokenList = tokenList;
        Document = new StringBuilder();
        for (Token token:tokenList) {
            Document.append(token.getName() + " ");
        }
    }

    public Atext(String[] s_Array) {
        Document = new StringBuilder();
        this.s_Array = s_Array;
        tokenList = new LinkedList<>();
        for (String s : s_Array) {
            Document.append(s + " ");
        }
    }

    public LinkedList<Token> Parse() {
        return null;
    }


}
//    Protected void removeDuplicates() {
//        for (int i = 0; i < tokenList.size(); i++) {
//            for (int j = i + 1; j < tokenList.size(); j++) {
//                if (tokenList.get(i).equals(tokenList.get(j)))
//                    tokenList.remove(j);
//            }
//        }
//    }
