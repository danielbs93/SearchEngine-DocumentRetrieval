package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 * this class responsible for expression with "-" and range between numbers.
 * the numbers are parsed by NumParser
 */
public class RangedParser extends Anumbers {

    public RangedParser() {
        super();
    }

    public RangedParser(LinkedList<Token> tokenList) {
        super(tokenList);
    }

    public RangedParser(Token token) {
        super(token);
    }

    @Override
    public LinkedList<Token> Parse() {
        //case: word-word-...-word or instead of word there is a number
        Token result = new Token();
        if (tokenList.size() == 1) {
            String term = tokenList.remove().getName();
            LinkedList<Token> list;
            String[] arrOfStrings = term.split("-");
            for (int i = 0; i < arrOfStrings.length; i++) {
                Token word = new Token(arrOfStrings[i]);
                if (word.isNumeric()) {
                    NumParser p = new NumParser(word);
                    list = p.Parse();
                    while (!list.isEmpty()) {
                        String current = list.remove().getName();
                        result.setName(current);
                    }
                }
                else {
                    String current = result.getName();
                    result.setName(current + "-" + word.getName());
                }
                if (i < arrOfStrings.length - 1)
                    result.setName(result.getName() + "-");
            }
            tokenList.clear();
            tokenList.add(result);
        }
        //case: "Between 18 and 24" --> 18-24
        if (tokenList.size() == 4) {
            Token second = tokenList.get(1);
            Token fourth = tokenList.get(3);
            LinkedList<Token> list;
            NumParser p = new NumParser(second);
            list = p.Parse();
            result = new Token(list.remove().getName());
            p = new NumParser(fourth);
            list = p.Parse();
            String current = result.getName();
            result.setName(current + "-" + list.remove().getName());
            tokenList.clear();
            tokenList.add(result);

        }
        return tokenList;
    }
}
