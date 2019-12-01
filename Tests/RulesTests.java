import Rules.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RulesTests {
    //    @Test
//    void name() {
//        dateParseTest();
//
//    }


    @Test
    public void percentageParseTest(){
        LinkedList<Token> check = new LinkedList<>();
        Token excpect;
        check.addLast(new Token("120"));
        check.addLast(new Token("%"));
        excpect = new Token("120%");
        PercentageParser test = new PercentageParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120%'");

        check.clear();
//        excpect.clear();
        check.addLast(new Token("12.12345"));
        check.addLast(new Token("percent"));
        excpect = new Token("12.123%");
        test = new PercentageParser(check);
        assertEqual(excpect,test.Parse(),"need to be '12.123%'");

        check.clear();
//        excpect.clear();
        check.addLast(new Token("12.12345"));
        check.addLast(new Token("percentage"));
        excpect = new Token("12.123%");
        test = new PercentageParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.123%'");
    }
    @Test
    public void priceParserTest(){
        LinkedList<Token> check = new LinkedList<>();
        Token excpect;
        check.addLast(new Token("$120"));
        excpect = new Token("120 Dollars");
//        excpect.addLast(new Token("Dollars"));
        PriceParser test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120 Dollars'");

        check.clear();
        check.addLast(new Token("100"));
        check.addLast(new Token("dollars"));
        excpect = new Token("100 Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '100 Dollars'");

        check.clear();
        check.addLast(new Token("1.796"));
        check.addLast(new Token("dollars"));
        excpect = new Token("1.796 Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1.796 Dollars'");

        check.clear();
        check.addLast(new Token("$450,000"));
        excpect = new Token("450,000 Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '450,000 Dollars'");

        check.clear();
        check.addLast(new Token("120"));
        check.addLast(new Token("3/4"));
        check.addLast(new Token("dollars"));
        excpect = new Token("120 3/4 Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120 3/4 Dollars'");

        check.clear();
        check.addLast(new Token("1,100,000"));
        check.addLast(new Token("dollars"));
        excpect = new Token("1.1 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1.1 M Dollars'");

        check.clear();
        check.addLast(new Token("$450,000,000"));
        excpect = new Token("450 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '450 M Dollars'");

        check.clear();
        check.addLast(new Token("$150"));
        check.addLast(new Token("million"));
        excpect = new Token("150 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '150 M Dollars'");

        check.clear();
        check.addLast(new Token("20.6m"));
        check.addLast(new Token("dollars"));
        excpect = new Token("20.6 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '20.6 M Dollars'");

        check.clear();
        check.addLast(new Token("30"));
        check.addLast(new Token("m"));
        check.addLast(new Token("dollars"));
        excpect = new Token("30 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '30 M Dollars'");

        check.clear();
        check.addLast(new Token("$101"));
        check.addLast(new Token("billion"));
        excpect = new Token("101000 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '101000 M Dollars'");

        check.clear();
        check.addLast(new Token("102bn"));
        check.addLast(new Token("dollars"));
        excpect = new Token("102000 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '102000 M Dollars'");

        check.clear();
        check.addLast(new Token("100"));
        check.addLast(new Token("billion"));
        check.addLast(new Token("U.S."));
        check.addLast(new Token("dollars"));
        excpect = new Token("100000 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '100 M Dollars'");

        check.clear();
        check.addLast(new Token("320"));
        check.addLast(new Token("million"));
        check.addLast(new Token("U.S."));
        check.addLast(new Token("dollars"));
        excpect = new Token("320 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '320 M Dollars'");

        check.clear();
        check.addLast(new Token("400"));
        check.addLast(new Token("trillion"));
        check.addLast(new Token("U.S."));
        check.addLast(new Token("dollars"));
        excpect = new Token("400000000 M Dollars");
//        excpect.addLast(new Token("Dollars"));
        test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '400000000 M Dollars'");

    }
    @Test
    public void numParseTest(){
        LinkedList<Token> check = new LinkedList<>();
        Token excpect;
        check.addLast(new Token("10.12345"));
        excpect = new Token("10.123");
        NumParser test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '10.123'");

        check.clear();
//        excpect.clear();
        check.addLast(new Token("12345"));
        excpect = new Token("12.345K");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.345K'");

        check.clear();
//        excpect.clear();
        check.addLast(new Token("12345678"));
        excpect = new Token("12.345M");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.345M'"); // need to check why ze magel lelmala

        check.clear();
//        excpect.clear();
        check.addLast(new Token("123456889111"));
        excpect = new Token("123.456B");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '123.456B'"); // same like million

        check.clear();
//        excpect.clear();
        check.addLast(new Token("12"));
        check.addLast(new Token("Thousand"));
        excpect = new Token("12K");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12K'");

        check.clear();
//        excpect.clear();
        check.addLast(new Token("1200"));
        check.addLast(new Token("Million"));
        excpect = new Token("1.2B");
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1.2B'");

        check.clear();
//        excpect.clear();
        check.addLast(new Token("120"));
        check.addLast(new Token("3/4"));
        excpect = new Token("120 3/4");
//        excpect.addLast(new Token("3/4"));
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120 3/4'");

    }
    @Test
    public void dateParseTest (){
        LinkedList<Token> check = new LinkedList<>();
        Token excpect;
        check.addLast(new Token("25"));
        check.addLast(new Token("Jun"));
        check.addLast(new Token("1992"));
        excpect = new Token("06-25-1992");
        DatesParser test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '06-25-1992'");

        check.clear();

        check.addLast(new Token("Jun"));
        check.addLast(new Token("25"));
        check.addLast(new Token("1992"));
//        excpect.clear();
        excpect = new Token("06-25-1992");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '06-25-1992'");

        check.clear();
        check.addLast( new Token("MAY"));
        check.addLast( new Token("01"));
//        excpect.clear();
        excpect = new Token("05-01");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '05-01'");

        check.clear();
        check.addLast( new Token("01"));
        check.addLast( new Token("MAY"));
//        excpect.clear();
        excpect = new Token("05-01");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '05-01'");

        check.clear();
        check.addLast( new Token("1980"));
        check.addLast( new Token("APRIL"));
        excpect = new Token("1980-04");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1980-04'");

        check.clear();
        check.addLast( new Token("APRIL"));
        check.addLast( new Token("1980"));
//        excpect.clear();
        excpect = new Token("1980-04");
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1980-04'");


    }

    @Test
    public void RangeParseTest(){
        LinkedList<Token> check = new LinkedList<>();
        Token excpect;
        check.addLast(new Token("one"));
        check.addLast(new Token("-"));
        check.addLast(new Token("by"));
        check.addLast(new Token("-"));
        check.addLast(new Token("one"));
        excpect = new Token("one-by-one");
        RangedParser test = new RangedParser(check);

        assertEqual(excpect, test.Parse(),"need to be 'one-by-one'");

        check.clear();
        check.addLast( new Token("two-by-two-by-two"));
        excpect = new Token("two-by-two-by-two");
        test = new RangedParser(check);

        assertEqual(excpect,test.Parse(),"need to be 'two-by-two-by-two'");

        check.clear();
        check.addLast( new Token("between"));
        check.addLast( new Token("18256"));
        check.addLast( new Token("and"));
        check.addLast( new Token("40258"));
        excpect = new Token("18.256K-40.258K");
        test = new RangedParser(check);

        assertEqual(excpect,test.Parse(),"need to be '18.256K-40.258K'");

        check.clear();
        check.addLast( new Token("12.536258-123456889111"));
        excpect = new Token("12.536-123.456B");
        test = new RangedParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.536-123.456B'");

    }

    @Test
    public void EntitiesParserTest() {
        String sentence = "Albanian President Sali Berisha held a meeting with  Ibrahim Rugova, president of the republic of Kosova. First Both presidents assessed as very fruitful and useful first the meeting of Mr. Rugova with President Clinton and the talks he held at the Department of State. They pointed out that these talks are a clear expression of a proper understanding of the necessity to solve the problem of Kosova on the part of President Clinton's  administration. The difficult situation of the Albanians in Kosova and the situation in the region were at the focus of the Berisha-Rugova meeting. Both presidents expressed full support for NATO's  decision on the ultimatum and are of the opinion that the recognition of the Former Yugoslav Republic of Macedonia by the United States contributes to the stability in the south of the Balkans.";
        EntitiesParser es = new EntitiesParser(sentence);
        LinkedList<Token> check = es.Parse();
        LinkedList<Token> expected = new LinkedList<>();
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
        LinkedList<Token> check = es.Parse();
        LinkedList<Token> expected = new LinkedList<>();
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

    private void assertEqual(LinkedList<Token> excpect,LinkedList<Token> check, String msg){
        LinkedList<String> ex = new LinkedList<>();
        LinkedList<String> ch = new LinkedList<>();
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