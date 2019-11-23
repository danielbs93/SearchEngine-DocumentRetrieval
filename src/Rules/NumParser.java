package Rules;

import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class NumParser extends Anumbers {
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
    public LinkedList<Token> Parse() {
        Token token;
        double num;
        if (tokenList.size() == 1){
            token = tokenList.getFirst();
            if (!token.isNumeric()) // need to check? who will call this fun
                return null;
            num = new Double(token.getName());
            if (num < 1000)
                return tokenList;
            else if (num < 1000000){
                num = num/1000;
                tokenList.getFirst().setName(String.valueOf(num) + "K");
            }
            else if (num < 1000000000){
                num = num/1000000;
                tokenList.getFirst().setName(String.valueOf(num) + "M");
            }
            else if ( num >= 1000000000){
                num = num/1000000000;
                tokenList.getFirst().setName(String.valueOf(num) + "B");
            }
            else
                return null;
            return tokenList;
        }
        else if (tokenList.size() == 2){
            token = tokenList.get(2);
            if (!token.isNumeric()){
                if (token.getName() == "Thousand" || token.getName() == "thousand" ){
                    Token number = tokenList.getFirst();

                }
                if (token.getName() == "Million" || token.getName() == "million" ){

                }
                if (token.getName() == "Billion" || token.getName() == "billion" ){

                }
            }
        }
        return null;
    }
}
