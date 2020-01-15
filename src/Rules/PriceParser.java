package Rules;

import java.util.ArrayList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class PriceParser extends Anumbers {

//    private DecimalFormat df3 = new DecimalFormat("#.###");

    public PriceParser() {
        super();
    }

    public PriceParser(ArrayList<Token> tokenList) {
        super(tokenList);
    }

    public PriceParser(Token token) {
        super(token);
    }

    @Override
    public Token Parse() {

        //case 1: $X
        int position = tokenList.get(0).getPosition();
        tokenList.get(0).setName(tokenList.get(0).getName().replaceAll("O", "0"));
        tokenList.get(0).setName(tokenList.get(0).getName().replaceAll("o", "0"));
        // $x-unit
        if (tokenList.size() == 1 && tokenList.get(0).getName().contains("-")) {
            Token token = tokenList.remove(0);
            String[] dollarAndUnit = token.getName().split("-");
            tokenList.add(new Token(dollarAndUnit[0]));
            tokenList.add(new Token(dollarAndUnit[1]));
        }
        //$xunit
        else if (tokenList.size() == 1 && Character.isLetter(tokenList.get(0).getName().charAt(tokenList.get(0).getName().length() - 1))) {
            String name = tokenList.get(0).getName();
            String dollarNum = name.substring(1), unit = "Dollars";
            Token number = new Token(dollarNum);
            Token Unit = new Token(unit);
            tokenList.remove(0);
            tokenList.add(number);
            tokenList.add(Unit);
        }
        if (tokenList.size() == 1) {
            String price = tokenList.get(0).getName().substring(1);
            if (!(new Token(price)).isNumeric())
                return tokenList.get(0);
            Result = ParseMyPrice(new Token(price));
            Result.setPosition(position);
            return Result;
        }
        //cases: X dollars,Xbn/m dollars, $X million/billion
        else if (tokenList.size() == 2) {
            Token first = tokenList.remove(0);
            Token second = tokenList.remove(0);
            if (first.getName().contains(","))
                first.setName(first.getName().replaceAll(",", ""));
            if (first.isNumeric() && isDollar(second)) {// X dollars
                Result = ParseMyPrice(first);
                Result.setPosition(position);
                return Result;
            } else if ((first.getName().contains("bn") || first.getName().contains("m")
                    || first.getName().contains("b") || first.getName().contains("M")
                    || first.getName().contains("B") || first.getName().contains("BN")) && isDollar(second)) {// Xbn/m dollars
                int indexOfLetter = -1;
                for (int i = 0; i < first.getName().length(); i++) {
                    if (Character.isLetter(first.getName().charAt(i))) {
                        indexOfLetter = i;
                        break;
                    }
                }
                if (first.getName().contains("bn") || first.getName().contains("B")
                        || first.getName().contains("BN") || first.getName().contains("b")) {
                    Token t_number = new Token(first.getName().substring(0, indexOfLetter));
                    if(t_number != null && t_number.getName().length()>0)
                        Result = ParseBillion(t_number);
                } else {
                    Token number = new Token(first.getName().substring(0, indexOfLetter));
                    if (number.getName().length() == 0)// just "million D/dollar"
                        number.setName("1");
                    number = makeMillion(number);
                    if(number != null)
                        Result = ParseMyPrice(number);
                    else
                        Result = new Token(first);
                }
                Result.setPosition(position);
                return Result;
            } else if (first.getName().contains("$")) {// $X billion/million
                Token t_number = new Token(first.getName().substring(1));
                if (t_number.isNumeric() && isMillion(second)) {
                    t_number = makeMillion(t_number);
                    Result = ParseMyPrice(t_number);
                } else if (t_number.isNumeric() && isBillion(second)) {
                    Result = ParseBillion(t_number);
                }
                Result.setPosition(position);
                return Result;
            }
        }
        // cases: X fraction dollars, X bn/m/million/billion dollars
        else if (tokenList.size() == 3) {
            Token first = tokenList.remove(0);
            Token second = tokenList.remove(0);
            Token third = tokenList.remove(0);
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
                    Result.setName(Result.getName() + " Dollars");
                }
                Result.setPosition(position);
                return Result;
            }
        }
        //cases: X billion/million/trillion U.S. Dollars
        else {// token size is 4
            Token first = tokenList.remove(0);
            Token second = tokenList.remove(0);
            Token third = tokenList.remove(0);
            Token fourth = tokenList.remove(0);
            if (isDollar(fourth)) {
                if (isMillion(second)) {
                    first = makeMillion(first);
                    Result = ParseMyPrice(first);
                } else if (isBillion(second)) {
                    Result = ParseBillion(first);
                } else if (isTrillion(second)) {
                    Double num = new Double(first.getName());
                    num *= 1000000;
                    first = new Token(df3.format(num));
                    Result.setName(first.getName() + " M");
                    Result.setName(Result.getName() + " Dollars");
                }
            }
        }
        Result.setPosition(position);
        return Result;
    }

    /**
     * @param t
     * @return token list after parsing the price number whether it's above 1M or not
     */
    private Token ParseMyPrice(Token t) {
        Token result = new Token();
        String price = t.getName();
        Double num = Double.parseDouble(price);
        if (num < 1000000) {
            //case: 1.7254632
            if (num.doubleValue() - ((int) num.doubleValue()) != 0)
                price = df3.format(num);
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
     *
     * @param s_num
     */
    private String addComma(String s_num) {
        if (s_num.contains(".")) {
            String number;
            String dot;
            number = s_num.substring(0, s_num.indexOf('.'));
            double num = Double.parseDouble(number);
            if (num < 1000)
                return s_num;
            number = innerComma(number);
            dot = s_num.substring(s_num.indexOf('.'), s_num.length());
            return (number + dot);
        } else
            return innerComma(s_num);
    }

    private String innerComma(String s_num) {
        if (s_num.length() == 6)//xxx,xxx
            return s_num.substring(0, 3) + "," + s_num.substring(3, 6);
        else if (s_num.length() == 5)//xx,xxx
            return s_num.substring(0, 2) + "," + s_num.substring(2, 5);
        else if (s_num.length() == 4)//x,xxx
            return s_num.substring(0, 1) + "," + s_num.substring(1, 4);
        return s_num;
    }

    private Token makeMillion(Token t) {
        try {
            double num = Double.parseDouble(t.getName());
            num *= 1000000;
            return new Token(df3.format(num));
        }catch (NumberFormatException e){return null;}
    }

}
