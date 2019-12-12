package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class NumParser extends Anumbers {
//    private DecimalFormat df3 = new DecimalFormat("#.###");

    public NumParser() {
        super();
    }

    public NumParser(LinkedList<Token> tokenList) {
        super(tokenList);
    }

    public NumParser(Token token) {
        super(token);
    }

    @Override
    public Token Parse() {
        Token token = new Token();
        int position = tokenList.getFirst().getPosition();
        double num ;
        if (tokenList.size() == 1){
            num = Double.parseDouble(tokenList.getFirst().getName());
            token = getValue(num);
            tokenList.clear();
            //tokenList.addFirst(token);
            Result = new Token(token);
            Result.setPosition(position);
            return Result;
        }
        else if (tokenList.size() == 2){
            Token value = tokenList.get(1);
            if (!value.isNumeric()){
                num = Double.parseDouble(tokenList.getFirst().getName());
                if (value.getName().equals("Thousand") || value.getName().equals("thousand"))
                    num = (Double.parseDouble(tokenList.getFirst().getName()))*1000.0;

                else if (value.getName().equals("Million") || value.getName().equals("million"))
                    num = (Double.parseDouble(tokenList.getFirst().getName()))*1000000.0;

                else if (value.getName().equals("Billion") || value.getName().equals("billion"))
                    num = (Double.parseDouble(tokenList.getFirst().getName()))*1000000000.0;
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
        else if ( num < 1000000000000.0){
            num = num/1000000000;
            token.setName(df3.format(num) + "B");
        }
        else
            return null;
        return token;
    }
}