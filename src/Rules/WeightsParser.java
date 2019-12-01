package Rules;

import java.util.LinkedList;

public class WeightsParser extends Anumbers {

    public WeightsParser() {
        super();
    }

    public WeightsParser(LinkedList<Token> tokenList) {
        super(tokenList);
    }

    public WeightsParser(Token token) {
        super(token);
    }

    public WeightsParser(String[] s_array) {
        super(s_array);
    }

    @Override
    public Token Parse() {
        Token first = tokenList.remove();
        Token second = tokenList.remove();
        if (second.getName().equals("Kilogram") || second.getName().equals("Kg")
                || second.getName().equals("kilogram") || second.getName().equals("kg")) {
            String number = ParseKg(first);
            Result.setName(number);
        }
        else if (second.getName().equals("gram") || second.getName().equals("gr")
                || second.getName().equals("Gram") || second.getName().equals("Gr")
                || second.getName().equals("g") || second.getName().equals("G")) {
            String number = ParseGr(first);
            Result.setName(number);
        }
        else if (second.getName().equals("Ton") || second.getName().equals("ton")
                || second.getName().equals("Ton") || second.getName().equals("t")) {
            Result.setName(first.getName() + " Ton");
        }

        return Result;
    }

    /**
     * If the weight is Kilogram we will return weight in kg if its under 1000
     * or Ton otherwise
     * @param first
     * @return
     */
    private String ParseKg(Token first) {
        double weight = Double.parseDouble(first.getName());
        if (weight > 1000)
            return (df3.format(weight/1000) + " Ton");
        else
            return (df3.format(weight) + " Kg");
    }

    /**
     * If the weight is in gram units we will return weight in gram if its under 1000
     * or Kg if its between 1000 to 1000000
     * or Ton otherwise
     * @param first
     * @return
     */
    private String ParseGr(Token first) {
        double weight = Double.parseDouble(first.getName());
        if (weight < 1000)
            return (df3.format(weight) + " gr");
        else if (weight < 1000000)
            return (df3.format(weight/1000) + " Kg");
        else
            return (df3.format(weight/1000000) + " Ton");
    }
}
