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

    public QuotesParser(LinkedList<Token> s_Array) {
        super(s_Array);
    }

    /**
     * Extract all quotes from the doc.
     * @return list of token while each token is a complete quote
     */
    public LinkedList<Token> Parse() {
        LinkedList<Token> Result = new LinkedList<>();
        StringBuilder quote = new StringBuilder();
        for (int i = 0; i < tokenList.size(); i++)
            quote.append(tokenList.get(i).getName()+ " ");
        Result.get(0).setName(quote.substring(0,quote.length()));
        Result.get(0).setPosition(tokenList.getFirst().getPosition());
        return Result;
    }
}
