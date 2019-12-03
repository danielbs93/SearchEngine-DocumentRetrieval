package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 * Parse every quote to 1 token
 */
public class QuotesParser extends Atext {
    public QuotesParser() {
        super();
    }

    public QuotesParser(String document) {
        super(document);
    }

    public QuotesParser(String[] s_Array) {
        super(s_Array);
    }

    /**
     * Extract all quotes from the doc.
     * @return list of token while each token is a complete quote
     */
    public LinkedList<Token> Parse() {
        String quote;
        for (int i = 0; i < s_Array.length; i++) {
            if (s_Array[i].equals("\"")) {
                quote = s_Array[i];
                i++;
                while (!s_Array[i].equals("\"")) {
                    quote = quote + s_Array[i];
                    i++;
                }
                quote = quote + "\"";
                tokenList.add(new Token(quote));
                i++;
                quote = "";
            }

        }
        return tokenList;
    }
}
