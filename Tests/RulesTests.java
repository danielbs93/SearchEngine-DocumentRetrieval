import Rules.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RulesTests {
    //    @Test
//    void name() {
//        dateParseTest();
//
//    }


    @Test
    public void percentageParseTest(){
        ArrayList<Token> check = new ArrayList<>();
        Token excpect;
        check.add(new Token("120"));
        check.add(new Token("%"));
        excpect = new Token("120%");
        PercentageParser test = new PercentageParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120%'");

        check.clear();
//        excpect.clear();
        check.add(new Token("12.12345"));
        check.add(new Token("percent"));
        excpect = new Token("12.123%");
        test = new PercentageParser(check);
        assertEqual(excpect,test.Parse(),"need to be '12.123%'");

        check.clear();
//        excpect.clear();
        check.add(new Token("12.12345"));
        check.add(new Token("percentage"));
        excpect = new Token("12.123%");
        test = new PercentageParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.123%'");
    }
    @Test
    public void priceParserTest(){
        ArrayList<Token> check = new ArrayList<>();
        Token excpect;
        check.add(new Token("$120"));
        excpect = new Token("120 Dollars");
//        excpect.add(new Token("Dollars"));
        PriceParser test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120 Dollars'");

        check.clear();
        check.add(new Token("100"));
        check.add(new Token("dollars"));
        excpect = new Token("100 Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '100 Dollars'");

        check.clear();
        check.add(new Token("1.796"));
        check.add(new Token("dollars"));
        excpect = new Token("1.796 Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1.796 Dollars'");

        check.clear();
        check.add(new Token("$450,000"));
        excpect = new Token("450,000 Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '450,000 Dollars'");

        check.clear();
        check.add(new Token("120"));
        check.add(new Token("3/4"));
        check.add(new Token("dollars"));
        excpect = new Token("120 3/4 Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120 3/4 Dollars'");

        check.clear();
        check.add(new Token("1,100,000"));
        check.add(new Token("dollars"));
        excpect = new Token("1.1 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1.1 M Dollars'");

        check.clear();
        check.add(new Token("$450,000,000"));
        excpect = new Token("450 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '450 M Dollars'");

        check.clear();
        check.add(new Token("$150"));
        check.add(new Token("million"));
        excpect = new Token("150 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '150 M Dollars'");

        check.clear();
        check.add(new Token("20.6m"));
        check.add(new Token("dollars"));
        excpect = new Token("20.6 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '20.6 M Dollars'");

        check.clear();
        check.add(new Token("30"));
        check.add(new Token("m"));
        check.add(new Token("dollars"));
        excpect = new Token("30 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '30 M Dollars'");

        check.clear();
        check.add(new Token("$101"));
        check.add(new Token("billion"));
        excpect = new Token("101000 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '101000 M Dollars'");

        check.clear();
        check.add(new Token("102bn"));
        check.add(new Token("dollars"));
        excpect = new Token("102000 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '102000 M Dollars'");

        check.clear();
        check.add(new Token("100"));
        check.add(new Token("billion"));
        check.add(new Token("U.S."));
        check.add(new Token("dollars"));
        excpect = new Token("100000 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '100 M Dollars'");

        check.clear();
        check.add(new Token("320"));
        check.add(new Token("million"));
        check.add(new Token("U.S."));
        check.add(new Token("dollars"));
        excpect = new Token("320 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '320 M Dollars'");

        check.clear();
        check.add(new Token("400"));
        check.add(new Token("trillion"));
        check.add(new Token("U.S."));
        check.add(new Token("dollars"));
        excpect = new Token("400000000 M Dollars");
//        excpect.add(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '400000000 M Dollars'");

    }
    @Test
    public void numParseTest(){
        ArrayList<Token> check = new ArrayList<>();
        Token excpect;
        check.add(new Token("10.12345"));
        excpect = new Token("10.123");
        NumParser test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '10.123'");

        check.clear();
//        excpect.clear();
        check.add(new Token("12345"));
        excpect = new Token("12.345K");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.345K'");

        check.clear();
//        excpect.clear();
        check.add(new Token("12345678"));
        excpect = new Token("12.345M");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.345M'"); // need to check why ze magel lelmala

        check.clear();
//        excpect.clear();
        check.add(new Token("123456889111"));
        excpect = new Token("123.456B");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '123.456B'"); // same like million

        check.clear();
//        excpect.clear();
        check.add(new Token("12"));
        check.add(new Token("Thousand"));
        excpect = new Token("12K");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12K'");

        check.clear();
//        excpect.clear();
        check.add(new Token("1200"));
        check.add(new Token("Million"));
        excpect = new Token("1.2B");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1.2B'");

        check.clear();
//        excpect.clear();
        check.add(new Token("120"));
        check.add(new Token("3/4"));
        excpect = new Token("120 3/4");
//        excpect.add(new Token("3/4"));
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120 3/4'");

    }
    @Test
    public void dateParseTest (){
        ArrayList<Token> check = new ArrayList<>();
        Token excpect;
        check.add(new Token("25"));
        check.add(new Token("Jun"));
        check.add(new Token("1992"));
        excpect = new Token("06-25-1992");
        DatesParser test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '06-25-1992'");

        check.clear();

        check.add(new Token("Jun"));
        check.add(new Token("25"));
        check.add(new Token("1992"));
//        excpect.clear();
        excpect = new Token("06-25-1992");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '06-25-1992'");

        check.clear();
        check.add( new Token("MAY"));
        check.add( new Token("01"));
//        excpect.clear();
        excpect = new Token("05-01");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '05-01'");

        check.clear();
        check.add( new Token("01"));
        check.add( new Token("MAY"));
//        excpect.clear();
        excpect = new Token("05-01");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '05-01'");

        check.clear();
        check.add( new Token("1980"));
        check.add( new Token("APRIL"));
        excpect = new Token("1980-04");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1980-04'");

        check.clear();
        check.add( new Token("APRIL"));
        check.add( new Token("1980"));
//        excpect.clear();
        excpect = new Token("1980-04");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1980-04'");


    }

    @Test
    public void RangeParseTest(){
        ArrayList<Token> check = new ArrayList<>();
        Token excpect;
        check.add(new Token("one"));
        check.add(new Token("-"));
        check.add(new Token("by"));
        check.add(new Token("-"));
        check.add(new Token("one"));
        excpect = new Token("one-by-one");
        RangedParser test = new RangedParser(check);

        assertEqual(excpect, test.Parse(),"need to be 'one-by-one'");

        check.clear();
        check.add( new Token("two-by-two-by-two"));
        excpect = new Token("two-by-two-by-two");
        test = new RangedParser(check);

        assertEqual(excpect,test.Parse(),"need to be 'two-by-two-by-two'");

        check.clear();
        check.add( new Token("between"));
        check.add( new Token("18256"));
        check.add( new Token("and"));
        check.add( new Token("40258"));
        excpect = new Token("18.256K-40.258K");
        test = new RangedParser(check);

        assertEqual(excpect,test.Parse(),"need to be '18.256K-40.258K'");

        check.clear();
        check.add( new Token("12.536258-123456889111"));
        excpect = new Token("12.536-123.456B");
        test = new RangedParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.536-123.456B'");

    }

    @Test
    public void EntitiesParserTest() {
        String sentence = "Albanian President Sali Berisha held a meeting with  Ibrahim Rugova, president of the republic of Kosova. First Both presidents assessed as very fruitful and useful first the meeting of Mr. Rugova with President Clinton and the talks he held at the Department of State. They pointed out that these talks are a clear expression of a proper understanding of the necessity to solve the problem of Kosova on the part of President Clinton's  administration. The difficult situation of the Albanians in Kosova and the situation in the region were at the focus of the Berisha-Rugova meeting. Both presidents expressed full support for NATO's  decision on the ultimatum and are of the opinion that the recognition of the Former Yugoslav Republic of Macedonia by the United States contributes to the stability in the south of the Balkans.";
        EntitiesParser es = new EntitiesParser(sentence);
        ArrayList<Token> check = es.Parse();
        ArrayList<Token> expected = new ArrayList<>();
        expected.add(new Token("Albanian"));
        expected.add(new Token("President"));
        expected.add(new Token("Sali"));
        expected.add(new Token("Berisha"));
        expected.add(new Token("Ibrahim"));
        expected.add(new Token("Rugova"));
        expected.add(new Token("Kosova"));
        expected.add(new Token("Mr"));
        expected.add(new Token("Clinton"));
        expected.add(new Token("Department"));
        expected.add(new Token("State"));
        expected.add(new Token("Berisha-Rugova"));
        expected.add(new Token("NATO"));
        expected.add(new Token("Yugoslav"));
        expected.add(new Token("Republic"));
        expected.add(new Token("Macedonia"));
        expected.add(new Token("United"));
        expected.add(new Token("States"));
        expected.add(new Token("Balkans"));

        assertEqual(expected,check,"Wrong EntitiesParser");
    }

    @Test
    public void UpperLowerParserTest() {
        String sentence = "Albanian President Sali Berisha held a meeting with  Ibrahim Rugova, president of the republic of Kosova. First Both presidents assessed as very fruitful and useful first the meeting of Mr. Rugova with President Clinton and the talks he held at the Department of State. They pointed out that these talks are a clear expression of a proper understanding of the necessity to solve the problem of Kosova on the part of President Clinton's  administration. The difficult situation of the Albanians in Kosova and the situation in the region were at the focus of the Berisha-Rugova meeting. Both presidents expressed full support for NATO's  decision on the ultimatum and are of the opinion that the recognition of the Former Yugoslav Republic of Macedonia by the United States contributes to the stability in the south of the Balkans.";
        UpperLowerCaseParser es = new UpperLowerCaseParser(sentence);
        ArrayList<Token> check = es.Parse();
        ArrayList<Token> expected = new ArrayList<>();
        expected.add(new Token("Albanian"));
        expected.add(new Token("President"));
        expected.add(new Token("Sali"));
        expected.add(new Token("Berisha"));
        expected.add(new Token("Ibrahim"));
        expected.add(new Token("Rugova"));
        expected.add(new Token("Kosova"));
        expected.add(new Token("First"));
        expected.add(new Token("Both"));
        expected.add(new Token("Mr"));
        expected.add(new Token("Clinton"));
        expected.add(new Token("Department"));
        expected.add(new Token("State"));
        expected.add(new Token("They"));
        expected.add(new Token("The"));
        expected.add(new Token("Albanians"));
        expected.add(new Token("Berisha-Rugova"));
        expected.add(new Token("NATO"));
        expected.add(new Token("Former"));
        expected.add(new Token("Yugoslav"));
        expected.add(new Token("Republic"));
        expected.add(new Token("Macedonia"));
        expected.add(new Token("United"));
        expected.add(new Token("States"));
        expected.add(new Token("Balkans"));

        assertEqual(expected,check,"Wrong UpperLower");
    }

    @Test
    public void QuoteParserTest() {
        String sentence = "\" a b c d \"";
        String[] sent = sentence.split(" ");
        ArrayList<Token> sen = new ArrayList<>();
        for (String s:sent) {
            sen.add(new Token(s));
        }
        QuotesParser q = new QuotesParser(sen);
        Token check = q.Parse();
        String s = "\"";
        Token excpect = new Token(s+"abcd"+s);

        assertEqual(excpect,check,"Wrong in QuoteParser");
    }

    @Test
    public void WeightsParserTest() {
        String[] arr1 = {"1538124","gr"};
        String[] arr2 = {"19482","kg"};
        String[] arr3 = {"10","ton"};
        String[] arr4 = {"153","Kilogram"};
        String[] arr5 = {"948","gram"};
        String[] arr6 = {"148235","gr"};
        String[] arr7 = {"2.456","kilogram"};

        WeightsParser wp = new WeightsParser(arr1);
        Token ch1 = wp.Parse();
        Token expected = new Token("1.538 Ton");
        assertEqual(expected,ch1,"Weight should be 1.538 Ton");

        wp = new WeightsParser(arr2);
        ch1 = wp.Parse();
        expected = new Token("19.482 Ton");
        assertEqual(expected,ch1,"Weight should be 1.948 Ton");

        wp = new WeightsParser(arr3);
        ch1 = wp.Parse();
        expected = new Token("10 Ton");
        assertEqual(expected,ch1,"Weight should be 10 Ton");

        wp = new WeightsParser(arr4);
        ch1 = wp.Parse();
        expected = new Token("153 Kg");
        assertEqual(expected,ch1,"Weight should be 153 Kg");

        wp = new WeightsParser(arr5);
        ch1 = wp.Parse();
        expected = new Token("948 gr");
        assertEqual(expected,ch1,"Weight should be 948 gr");

        wp = new WeightsParser(arr6);
        ch1 = wp.Parse();
        expected = new Token("148.235 Kg");
        assertEqual(expected,ch1,"Weight should be 148.235 Kg");

        wp = new WeightsParser(arr7);
        ch1 = wp.Parse();
        expected = new Token("2.456 Kg");
        assertEqual(expected,ch1,"Weight should be 2.456 Kg");

    }

    private void assertEqual(ArrayList<Token> excpect,ArrayList<Token> check, String msg){
        ArrayList<String> ex = new ArrayList<>();
        ArrayList<String> ch = new ArrayList<>();
        for (int i = 0; i < excpect.size(); i++)
            ex.add(i,excpect.get(i).getName());
        for (int i = 0; i < check.size(); i++)
            ch.add(i,check.get(i).getName());
        assertEquals(ex,ch,msg);
    }
    private void assertEqual(Token excpect,Token check, String msg){
        assertEquals(excpect.getName(),check.getName(),msg);
    }


}