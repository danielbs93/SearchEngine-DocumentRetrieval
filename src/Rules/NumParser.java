package Rules;

import java.util.ArrayList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class NumParser extends Anumbers {

    public NumParser() {
        super();
    }

    public NumParser(ArrayList<Token> tokenList) {
        super(tokenList);
    }

    public NumParser(Token token) {
        super(token);
    }

    @Override
    public Token Parse() {
        Token token = new Token();
        int position = tokenList.get(0).getPosition();
        double num ;
        if (tokenList.size() == 1) {
            if (!tokenList.get(0).isNumeric())
                return tokenList.get(0);
            try {
                num = Double.parseDouble(tokenList.get(0).getName());
                token = getValue(num);
                tokenList.clear();
                //tokenList.addFirst(token);
                Result = new Token(token);
                Result.setPosition(position);
                return Result;
            }catch (NumberFormatException e){}
        }
        else if (tokenList.size() == 2){
            Token value = tokenList.get(1);
            boolean moreThanThousandBillion = false;
            if (!value.isNumeric()){
                num = Double.parseDouble(tokenList.get(0).getName());
                if (value.getName().equals("Thousand") || value.getName().equals("thousand"))
                    num = (Double.parseDouble(tokenList.get(0).getName()))*1000.0;

                else if (value.getName().equals("Million") || value.getName().equals("million"))
                    num = (Double.parseDouble(tokenList.get(0).getName()))*1000000.0;

                else if (value.getName().equals("Billion") || value.getName().equals("billion")) {
                    if (num < 1000)
                        num = (Double.parseDouble(tokenList.get(0).getName())) * 1000000000.0;
                    else
                        moreThanThousandBillion = true;
                }
                if (moreThanThousandBillion)
                    token = new Token(num+ "B");
                else
                    token = getValue(num);
                tokenList.clear();
                //tokenList.addFirst(token);
                Result = new Token(token);
                Result.setPosition(position);
                if (value.isFraction())
                    Result.setName(Result.getName() + " " + value.getName());
                return Result;
            }
        }
        return null;
    }

    /**
     *
     * @param num to limited
     * @return a limited value according to the rules
     */
    private Token getValue (double num){
        Token token = new Token();
        if (num < 1000)
            token.setName(df3.format(num));
        else if (num < 1000000.0){
            num = num/1000.0;
            token.setName(df3.format(num)+ "K");
        }
        else if (num < 1000000000.0){
            num = num/1000000;
            token.setName(df3.format(num) + "M");
        }
        else {//if ( num < 1000000000000.0){
            num = num/1000000000;
            token.setName(df3.format(num) + "B");
        }
//        else
//            return null;
        return token;
    }
}