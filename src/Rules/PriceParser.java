package Rules;

import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class PriceParser extends Anumbers {

    private DecimalFormat df3 = new DecimalFormat("#.###");

    public PriceParser() {
        super();
    }

    public PriceParser(LinkedList<Token> tokenList) {
        super(tokenList);
    }

    public PriceParser(Token token) {
        super(token);
    }

    @Override
    public LinkedList<Token> Parse() {
        //case 1: $X
        if (tokenList.size() == 1) {
            String price = tokenList.getFirst().getName().substring(1, tokenList.getFirst().getName().length() - 1);
            Token number = new Token(price);
            tokenList = ParseMyPrice(number);
            Token dollars = new Token("Dollars");
            tokenList.add(dollars);
            return tokenList;
        }
        //cases: X dollars,Xbn/m dollars, $X million/billion
        else if (tokenList.size() == 2) {
            Token first = tokenList.remove();
            Token second = tokenList.remove();
            if (first.isNumeric() && isDollar(second)) {// X dollars
                tokenList = ParseMyPrice(first);
                tokenList.add(new Token("Dollars"));
                return tokenList;
            } else if ((first.getName().contains("bn") || first.getName().contains("m")) && isDollar(second)) {// Xbn/m dollars
                if (first.getName().contains("bn")) {
                    Token t_number = new Token(first.getName().substring(0, first.getName().indexOf('b')));
                    tokenList = ParseBillion(t_number);
                } else {
                    Token number = new Token(first.getName().substring(0, first.getName().indexOf('m')));
                    tokenList = ParseMyPrice(number);
                }
                second.setName("Dollars");
                tokenList.add(second);
                return tokenList;
            } else if (first.getName().contains("$")) {// $X billion/million
                Token t_number = new Token(first.getName().substring(1, first.getName().length() - 1));
                if (second.getName().equals("million")) {
                    tokenList = ParseMyPrice(t_number);
                } else if (second.getName().equals("billion")) {
                    tokenList = ParseBillion(t_number);
                }
                tokenList.add(new Token("Dollars"));
                return tokenList;
            }
        }
        // cases: X fraction dollars, X bn/m dollars
        else if (tokenList.size() == 3) {
            Token first = tokenList.remove();
            Token second = tokenList.remove();
            Token third = tokenList.remove();
            if (isDollar(third)) {
                //X bn/m
                if (second.getName().contains("bn")) {
                    tokenList = ParseBillion(first);
                } else if (second.getName().contains("m")) {
                    tokenList = ParseMyPrice(first);
                }
                //X fraction
                else {
                    tokenList.add(first);
                    tokenList.add(second);
                }
                tokenList.add(new Token("Dollars"));
                return tokenList;
            }
        }
        //cases: X billion/million/trillion U.S. Dollars
        else {// token size is 4
            Token first = tokenList.remove();
            Token second = tokenList.remove();
            Token third = tokenList.remove();
            Token fourth = tokenList.remove();
            if (isDollar(fourth)) {
                if (second.getName().equals("million"))
                    tokenList = ParseMyPrice(first);
                else if (second.getName().equals("billion"))
                    tokenList = ParseBillion(first);
                else if (second.getName().equals("trillion")) {
                    Double num = new Double(first.getName());
                    num *= 1000000;
                    first = new Token(df3.format(num));
                    tokenList.add(first);
                    tokenList.add(new Token("T"));
                }
                tokenList.add(new Token("Dollars"));
            }
        }
        return tokenList;
    }

    /**
     * @param t
     * @return token list after parsing the price number whether it's above 1M or not
     */
    private LinkedList<Token> ParseMyPrice(Token t) {
        LinkedList<Token> result = new LinkedList<>();
        Double num = new Double(t.getName());
        if (num < 1000000) {
            if (num.doubleValue() - ((int) num.doubleValue()) != 0)
                result.add(new Token(df3.format(num)));
            else
                result.add(t);
        } else {
            num = num / 1000000;
            result.add(new Token(df3.format(num)));
            result.add(new Token("M"));
        }
        return result;
    }

    private LinkedList<Token> ParseBillion(Token t) {
        LinkedList<Token> result = new LinkedList<>();
        Double number = new Double(t.getName());
        number *= 1000;
        t = new Token(df3.format(number));
        result.add(t);
        result.add(new Token("Dollars"));
        return result;
    }

    private boolean isDollar(Token t) {
        if (t.getName().equals("Dollars") || t.getName().equals("dollars"))
            return true;
        return false;
    }
}
