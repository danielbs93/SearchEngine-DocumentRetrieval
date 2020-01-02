package Rules;

import java.util.ArrayList;

public class DistancesParser extends Anumbers {



    public DistancesParser() {
        super();
    }

    public DistancesParser(ArrayList<Token> tokenList) {
        super(tokenList);
    }

    public DistancesParser(Token token) {
        super(token);
    }

    public DistancesParser(String[] s_array) {
        super(s_array);
    }

    @Override
    public Token Parse() {
        int position = tokenList.get(0).getPosition();
        Token first = tokenList.remove(0);
        Token second = tokenList.remove(0);
        if (isMeter(second.getName())) {
            String number = ParseMeter(first);
            Result.setName(number);
        }
        else if (isCm(second.getName())) {
            String number = ParseCm(first);
            Result.setName(number);
        }
        else if (isKm(second.getName())) {
            Result.setName(first.getName() + "km");
        }
        Result.setPosition(position);
        return Result;
    }

    /**
     * If the distance is meter we will return distance in km if its above 1000
     * or Ton otherwise
     * @param first
     * @return
     */
    private String ParseMeter(Token first) {
        double weight = Double.parseDouble(first.getName());
        if (weight > 1000)
            return (df3.format(weight/1000) + " km");
        else
            return (df3.format(weight) + " m");
    }

    /**
     * If the distance is in cm units we will return distance in meter if its above 1000
     * or km if its between 1000 to 1000000
     * or Ton otherwise
     * @param first
     * @return
     */
    private String ParseCm(Token first) {
        double weight = Double.parseDouble(first.getName());
        if (weight < 1000)
            return (df3.format(weight) + " cm");
        else if (weight < 1000000)
            return (df3.format(weight/1000) + " meter");
        else
            return (df3.format(weight/1000000) + " km");
    }

    public static boolean isDistanceUnit(String unit) {
        if (unit != null)
            if (isKm(unit) || isCm(unit) || isMeter(unit))
                return true;
        return false;
    }

    private static boolean isCm(String unit) {
        if (unit.equals("Centimeter") || unit.equals("centimeter") || unit.equals("CM") || unit.equals("Cm")
                || unit.equals("cm") || unit.equals("c\"m") || unit.equals("centimeters") || unit.equals("Centimeters")
                || unit.equals("C\"m") || unit.equals("C\"M"))
            return true;
        return false;
    }

    private static boolean isKm(String unit) {
        if (unit.equals("KM") || unit.equals("Km") || unit.equals("km") || unit.equals("Kilometer")
                || unit.equals("Kilometers") || unit.equals("kilometer") || unit.equals("kilometers"))
            return true;
        return false;
    }

    private static boolean isMeter(String unit) {
        if (unit.equals("Meter") || unit.equals("meter")
                || unit.equals("Meters") || unit.equals("meters"))
            return true;
        return false;
    }
}
