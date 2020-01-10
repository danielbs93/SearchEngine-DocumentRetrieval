package Rules;

import java.util.ArrayList;
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

    public RangedParser(ArrayList<Token> tokenList) {
        super(tokenList);
    }

    public RangedParser(Token token) {
        super(token);
    }

    @Override
    public Token Parse() {
        //case: word-word-...-word or instead of word there is a number, no space between 'word' to '-'
        Token result = new Token();
        Token num;
        int position = tokenList.get(0).getPosition();
        if (tokenList.size() == 1) {
            String current;
            String term = tokenList.remove(0).getName();
            String[] arrOfStrings = term.split("-");
            for (int i = 0; i < arrOfStrings.length; i++) {
                Token word = new Token(arrOfStrings[i]);
                if (word.isNumeric()) {
                    NumParser p = new NumParser(word);
                    if (p != null) {
                        num = p.Parse();
                        current = num.getName();
                        if (i > 0)
                            result.setName(result.getName() + current);
                        else
                            result.setName(current);
                    } else {
                        current = result.getName();
                        if (i > 0)
                            result.setName(current + word.getName());
                        else
                            result.setName(word.getName());

                    }
                    if (i < arrOfStrings.length - 1)
                        result.setName(result.getName() + "-");
                } else if (word.getName().contains("$") && arrOfStrings.length == 2 && i + 1 < arrOfStrings.length) {
                    ArrayList<Token> price = new ArrayList<>();
                    price.add(word);
                    price.add(new Token(arrOfStrings[i + 1]));
                    PriceParser p = new PriceParser(price);
                    result = p.Parse();
                    break;
                }
            }
            if (result.getName().length() == 0 && arrOfStrings.length > 0) {
                StringBuilder name = new StringBuilder();
                for (String word : arrOfStrings) {
                    name.append(word + "-");
                }
                result.setName(name.substring(0, name.length() - 1));
            }
            tokenList.clear();
//            tokenList.add(result);
            Result = new Token(result);
            Result.setPosition(position);
            return Result;
        }
        //case: "Between 18 and 24" --> 18-24
        else if (tokenList.size() == 4) {
            Token second = tokenList.get(1);
            Token fourth = tokenList.get(3);
            NumParser p = new NumParser(second);
            num = p.Parse();
            if (!num.isNumeric())
                return num;
            result = new Token(num.getName());
            p = new NumParser(fourth);
            num = p.Parse();
            if (!num.isNumeric())
                return num;
            String current = result.getName();
            result.setName(current + "-" + num.getName());
            tokenList.clear();
//            tokenList.add(result);
            Result = new Token(result);
            Result.setPosition(position);
            return Result;
        }
        //case: "w1 - w2 - ... - wn", wi can be word or number, there is space between words and '-' meaning even places in tokenList is '-'
        else if (tokenList.size() % 2 != 0) {
            removeDashes();
            for (int i = 0; i < tokenList.size(); i++) {
                Token word = new Token(tokenList.get(i));
                if (word.isNumeric()) {
                    NumParser p = new NumParser(word);
                    num = p.Parse();
                    String current = num.getName();
                    result.setName(current);
                } else {
                    result.setName(result.getName() + word.getName());
                }
                if (i < tokenList.size() - 1)
                    result.setName(result.getName() + "-");
            }
            Result = new Token(result);
        }
        Result.setPosition(position);
        return Result;
    }

    /**
     * In cases which token list created by following format: "w1 - w2 - ... - w3" with spaces
     */
    private void removeDashes() {
        LinkedList<Token> list = new LinkedList<>();
        list.addAll(tokenList);
        tokenList.clear();
        for (int i = 0; i < list.size(); i++) {
            if (!(list.get(i).getName().equals("-")))
                tokenList.add(list.get(i));
        }
    }
}
