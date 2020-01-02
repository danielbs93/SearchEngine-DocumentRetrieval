package Rules;

import java.util.ArrayList;

public class WeightsParser extends Anumbers {

    public WeightsParser() {
        super();
    }

    public WeightsParser(ArrayList<Token> tokenList) {
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
        int position = tokenList.get(0).getPosition();
        Token first = tokenList.remove(0);
        Token second = tokenList.remove(0);
        if (isKg(second.getName())) {
            String number = ParseKg(first);
            Result.setName(number);
        }
        else if (isGr(second.getName())) {
            String number = ParseGr(first);
            Result.setName(number);
        }
        else if (isTon(second.getName())) {
            Result.setName(first.getName() + " Ton");
        }
        Result.setPosition(position);
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

    public static boolean isWeightUnit(String unit) {
        if (unit != null)
            if (isKg(unit) || isGr(unit) || isTon(unit))
                return true;
        return false;
    }

    private static boolean isGr(String unit) {
        if (unit.equals("gram") || unit.equals("gr") || unit.equals("Grams") || unit.equals("grams")
                || unit.equals("Gram") || unit.equals("Gr")
                || unit.equals("g") || unit.equals("G") || unit.equals("Gs") || unit.equals("gs"))
            return true;
        return false;
    }

    private static boolean isTon(String unit) {
        if (unit.equals("Ton") || unit.equals("ton") || unit.equals("tons") || unit.equals("Tons")
                || unit.equals("T") || unit.equals("t"))
                return true;
        return false;
    }

    private static boolean isKg(String unit) {
        if (unit.equals("Kilogram") || unit.equals("Kg")
                || unit.equals("kilogram") || unit.equals("kg")
                || unit.equals("KG") || unit.equals("Kgs") || unit.equals("KGs")
                || unit.equals("kgs") || unit.equals("kilograms") || unit.equals("Kilograms"))
            return true;
        return false;
    }
}
