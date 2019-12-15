package Rules;

import java.util.ArrayList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 * pay attention that we must save months name and their short cuts thus we have text too in those terms
 */
public class DatesParser extends Anumbers {
    public DatesParser() {
    }
    public DatesParser(ArrayList<Token> tokenList) {
        super(tokenList);
    }
    public DatesParser(Token token) {
        super(token);
    }

    @Override
    public Token Parse() {
        int num1=0,num2=0;
        String month = "";
        Token token = new Token();
        token.setPosition(tokenList.get(0).getPosition());
        if (tokenList.size() == 2){
            if (tokenList.get(0).isNumeric() && !tokenList.get(1).isNumeric()) { // 04 Jan
                month = whichMonth(tokenList.get(1).getName());
                num1 = Integer.parseInt(tokenList.get(0).getName());
            }
            else if (tokenList.get(1).isNumeric() && !tokenList.get(0).isNumeric()) { // Jan 04
                month = whichMonth(tokenList.get(0).getName());
                num1 = Integer.parseInt(tokenList.get(1).getName());
            }
            if (month.length() > 0) {
                if (num1 < 10)
                    token.setName(month + "-0" + num1);
                else if (num1 < 100)
                    token.setName(month + "-" + num1);
                else
                    token.setName(num1 + "-" + month);
            }
                else
                    return null;
            if (token.getName().length()==0)
                return null;
            tokenList.clear();
            //tokenList.add(token);
            this.Result = new Token(token);
            this.Result.setPosition(token.getPosition());
            return Result;
        }
        if (tokenList.size() == 3){
            if (tokenList.get(0).isNumeric() && !tokenList.get(1).isNumeric() && tokenList.get(2).isNumeric()) { // 28 Feb 1995
                month = whichMonth((tokenList.get(1).getName()));
                num1 = Integer.parseInt(tokenList.get(0).getName());
                num2 = Integer.parseInt(tokenList.get(2).getName());
            }
            else if (tokenList.get(1).isNumeric() && !tokenList.get(0).isNumeric() && tokenList.get(2).isNumeric()) { // Feb 28 1995
                month = whichMonth((tokenList.get(0).getName()));
                num1 = Integer.parseInt(tokenList.get(1).getName());
                num2 = Integer.parseInt(tokenList.get(2).getName());
            }
            else if (tokenList.get(1).isNumeric() && !tokenList.get(2).isNumeric() && tokenList.get(0).isNumeric()) { //  28 1995 Feb
                month = whichMonth((tokenList.get(2).getName()));
                num1 = Integer.parseInt(tokenList.get(1).getName());
                num2 = Integer.parseInt(tokenList.get(0).getName());
            }
            if (month.length() > 0){
                if (num1 < num2){
                    if (num1 < 10)
                        token.setName(month + "-0" + num1 + "-"  + num2);
                    else if (num1 < 32)
                        token.setName(month + "-" + num1 + "-" + num2);
                }
                else {
                    if (num2 < 10)
                        token.setName(month + "-0" + num2 + "-" + num1);
                    else if (num1 < 32)
                        token.setName(month + "-" + num1 + "-" + num2);
                }
            }
            else
                return null;
            if (token.getName().length()==0)
                return null;
            tokenList.clear();
            //tokenList.add(token);
            Result = new Token(token);
            this.Result.setPosition(token.getPosition());
            return Result;

        }
        return null;
    }

    /**
     *
     * @param str describe month
     * @return the number of the month or zero if its illegal string
     */
    private static String whichMonth (String str){
        if (str.equals("January")||str.equals("JANUARY")||str.equals("Jan")||str.equals("JAN"))
            return "01";
        else if (str.equals("February")||str.equals("FEBRUARY")||str.equals("Feb")||str.equals("FEB"))
            return "02";
        else if (str.equals("March")||str.equals("MARCH")||str.equals("Mar")||str.equals("MAR"))
            return "03";
        else if (str.equals("April")||str.equals("APRIL")||str.equals("Apr")||str.equals("APR"))
            return "04";
        else if (str.equals("May")||str.equals("MAY"))
            return "05";
        else if (str.equals("June")||str.equals("JUNE")||str.equals("Jun")||str.equals("JUN"))
            return "06";
        else if (str.equals("July")||str.equals("JULY")||str.equals("Jul")||str.equals("JUL"))
            return "07";
        else if (str.equals("August")||str.equals("AUGUST")||str.equals("Aug")||str.equals("AUG"))
            return "08";
        else if (str.equals("September")||str.equals("SEPTEMBER")||str.equals("Sep")||str.equals("SEP"))
            return "09";
        else if (str.equals("October")||str.equals("OCTOBER")||str.equals("Oct")||str.equals("OCT"))
            return "10";
        else if (str.equals("November")||str.equals("NOVEMBER")||str.equals("Nov")||str.equals("NOV"))
            return "11";
        else if (str.equals("December")||str.equals("DECEMBER")||str.equals("Dec")||str.equals("DEC"))
            return "12";
        return "";
    }

    public static boolean isDate (String month){
        if (whichMonth(month).length()>0)
            return true;
        return false;
    }
}
