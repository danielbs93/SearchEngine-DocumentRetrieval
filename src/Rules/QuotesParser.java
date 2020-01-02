package Rules;

import java.util.ArrayList;

///**
// * Created by: Daniel Ben-Simon & Eran Toutian
// * Parse every quote to 1 token
// */
//public class QuotesParser implements IParser {
//    private ArrayList<Token> tokenList;
//
//    public QuotesParser() {
//        tokenList = new ArrayList<>();
//    }
//
//
//    public QuotesParser(ArrayList<Token> s_Array) {
//
//        tokenList = s_Array;
//    }
//
//    /**
//     * Extract all quotes from the doc.
//     * @return list of token while each token is a complete quote
//     */
//    public Token Parse() {
//        Token Result = new Token();
//        StringBuilder quote = new StringBuilder();
//        for (int i = 0; i < tokenList.size(); i++)
//            quote.append(tokenList.get(i).getName()+ " ");
//        Result.setName(quote.substring(0,quote.length()));
//        Result.setPosition(tokenList.get(0).getPosition());
//        return Result;
//    }
//}
