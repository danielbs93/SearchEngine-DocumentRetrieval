package Rules;

import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class PriceParser extends Anumbers {

//    private DecimalFormat df3 = new DecimalFormat("#.###");

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
    public Token Parse() {
        //case 1: $X
        if (tokenList.size() == 1) {
            String price = tokenList.getFirst().getName().substring(1);
//            Token number = new Token(price);
            Result = ParseMyPrice(new Token(price));
//            Token dollars = new Token("Dollars");
//            tokenList.addLast(new Token("Dollars"));
            return Result;
        }
        //cases: X dollars,Xbn/m dollars, $X million/billion
        else if (tokenList.size() == 2) {
            Token first = tokenList.remove();
            Token second = tokenList.remove();
            if (first.getName().contains(","))
                first.setName(first.getName().replaceAll(",",""));
            if (first.isNumeric() && isDollar(second)) {// X dollars
                Result = ParseMyPrice(first);
//                tokenList.add(new Token("Dollars"));
                return Result;
            } else if ((first.getName().contains("bn") || first.getName().contains("m")) && isDollar(second)) {// Xbn/m dollars
                if (first.getName().contains("bn")) {
                    Token t_number = new Token(first.getName().substring(0, first.getName().indexOf('b')));
                    Result = ParseBillion(t_number);
                } else {
                    Token number = new Token(first.getName().substring(0, first.getName().indexOf('m')));
                    number = makeMillion(number);
                    Result = ParseMyPrice(number);
                }
//                second.setName("Dollars");
//                tokenList.add(second);
                return Result;
            } else if (first.getName().contains("$")) {// $X billion/million
                Token t_number = new Token(first.getName().substring(1));
                if (isMillion(second)) {
                    t_number = makeMillion(t_number);
                    Result = ParseMyPrice(t_number);
                } else if (isBillion(second)) {
                    Result = ParseBillion(t_number);
                }
//                tokenList.add(new Token("Dollars"));
                return Result;
            }
        }
        // cases: X fraction dollars, X bn/m/million/billion dollars
        else if (tokenList.size() == 3) {
            Token first = tokenList.remove();
            Token second = tokenList.remove();
            Token third = tokenList.remove();
            if (isDollar(third)) {
                //X bn/m/million/billion
                if (second.getName().equals("bn") || isBillion(second)) {
                    Result = ParseBillion(first);
                } else if (isMillion(second)) {
                    first = makeMillion(first);
                    Result = ParseMyPrice(first);
                }
                //X fraction
                else {
                    Result.setName(first.getName() + " " + second.getName());
//                    tokenList.add(first);
//                    tokenList.add(second);
                    Result.setName(Result.getName() + " Dollars");
                }
//                tokenList.add(new Token("Dollars"));
                return Result;
            }
        }
        //cases: X billion/million/trillion U.S. Dollars
        else {// token size is 4
            Token first = tokenList.remove();
            Token second = tokenList.remove();
            Token third = tokenList.remove();
            Token fourth = tokenList.remove();
            if (isDollar(fourth)) {
                if (isMillion(second)) {
                    first = makeMillion(first);
                    Result = ParseMyPrice(first);
                }
                else if (isBillion(second)) {
                    Result = ParseBillion(first);
                }
                else if (isTrillion(second)) {
                    Double num = new Double(first.getName());
                    num *= 1000000;
                    first = new Token(df3.format(num));
//                    tokenList.add(first);
//                    tokenList.add(new Token("T"));
                    Result.setName(first.getName() + " M");
                    Result.setName(Result.getName() + " Dollars");
                }
//                tokenList.add(new Token("Dollars"));
            }
        }
        return Result;
    }

    /**
     * @param t
     * @return token list after parsing the price number whether it's above 1M or not
     */
    private Token ParseMyPrice(Token t) {
        Token result = new Token();
        String price = t.getName();
        price = price.replaceAll(",","");
        Double num = new Double(price);
        if (num < 1000000) {
            //case: 1.7254632
            if (num.doubleValue() - ((int) num.doubleValue()) != 0)
                price = df3.format(num);
                //result.add(new Token(df3.format(num)));
            price = addComma(price);
            result.setName(price + " Dollars");
        } else {
            num = num / 1000000;
            result.setName(df3.format(num) + " M Dollars");
        }
        return result;
    }

    private Token ParseBillion(Token t) {
        Token result = new Token();
        Double number = new Double(t.getName());
        number *= 1000;
        result.setName(df3.format(number) + " M Dollars");
        return result;
    }

    private boolean isDollar(Token t) {
        if (t.getName().equals("Dollars") || t.getName().equals("dollars"))
            return true;
        return false;
    }

    private boolean isMillion(Token t) {
        if (t.getName().equals("m") || t.getName().equals("million") || t.getName().equals("M") || t.getName().equals("Million"))
            return true;
        return false;
    }

    private boolean isBillion(Token t) {
        if (t.getName().equals("b") || t.getName().equals("billion") || t.getName().equals("B") || t.getName().equals("Billion"))
            return true;
        return false;
    }

    private boolean isTrillion(Token t) {
        if (t.getName().equals("t") || t.getName().equals("trillion") || t.getName().equals("T") || t.getName().equals("Trillion"))
            return true;
        return false;
    }

    /**
     * Adding coma to numbers whom less than 1M
     * @param s_num
     */
    private String addComma(String s_num) {
        if (s_num.contains(".")) {
            String number;
            String dot;
            number = s_num.substring(0,s_num.indexOf('.'));
            double num = Double.parseDouble(number);
            if (num < 1000)
                return s_num;
            number = innerComma(number);
            dot = s_num.substring(s_num.indexOf('.'), s_num.length());
            return (number + dot);
        }else
            return innerComma(s_num);
    }
    private String innerComma(String s_num){
        if (s_num.length() == 6)//xxx,xxx
            return s_num.substring(0, 3) + "," + s_num.substring(3, 6);
        else if (s_num.length() == 5)//xx,xxx
            return s_num.substring(0, 2) + "," + s_num.substring(2, 5);
        else if (s_num.length() == 4)//x,xxx
            return s_num.substring(0, 1) + "," + s_num.substring(1, 4);
        return s_num;
    }

    private Token makeMillion(Token t) {
        double num = Double.parseDouble(t.getName());
        num *= 1000000;
        return new Token(df3.format(num));
    }
}
