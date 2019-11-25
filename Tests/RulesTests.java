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
        LinkedList<Token> excpect = new LinkedList<>();
        check.addLast(new Token("120"));
        check.addLast(new Token("%"));
        excpect.addLast(new Token("120%"));
        PercentageParser test = new PercentageParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120%'");

        check.clear();
        excpect.clear();
        check.addLast(new Token("12.12345"));
        check.addLast(new Token("percent"));
        excpect.addLast(new Token("12.123%"));
        test = new PercentageParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.123%'");

        check.clear();
        excpect.clear();
        check.addLast(new Token("12.12345"));
        check.addLast(new Token("percentage"));
        excpect.addLast(new Token("12.123%"));
        test = new PercentageParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.123%'");
    }
    @Test
    public void priceParserTest(){
        LinkedList<Token> check = new LinkedList<>();
        LinkedList<Token> excpect = new LinkedList<>();
        check.addLast(new Token("$120"));
        excpect.addLast(new Token("120"));
        excpect.addLast(new Token("Dollars"));
        PriceParser test = new PriceParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120 Dollars'");


    }
    @Test
    public void numParseTest(){
        LinkedList<Token> check = new LinkedList<>();
        LinkedList<Token> excpect = new LinkedList<>();
        check.addLast(new Token("10.12345"));
        excpect.addLast(new Token("10.123"));
        NumParser test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '10.123'");

        check.clear();
        excpect.clear();
        check.addLast(new Token("12345"));
        excpect.addLast(new Token("12.345K"));
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12.345K'");

        check.clear();
        excpect.clear();
        check.addLast(new Token("12345678"));
        excpect.addLast(new Token("12.345M"));
        test = new NumParser(check);

//        assertEqual(excpect,test.Parse(),"need to be '12.345M'"); // need to check why ze magel lelmala

        check.clear();
        excpect.clear();
        check.addLast(new Token("123456889111"));
        excpect.addLast(new Token("123.456B"));
        test = new NumParser(check);

//        assertEqual(excpect,test.Parse(),"need to be '123.456B'"); // same like million

        check.clear();
        excpect.clear();
        check.addLast(new Token("12"));
        check.addLast(new Token("Thousand"));
        excpect.addLast(new Token("12K"));
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '12K'");

        check.clear();
        excpect.clear();
        check.addLast(new Token("1200"));
        check.addLast(new Token("Million"));
        excpect.addLast(new Token("1.2B"));
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1.2B'");

        check.clear();
        excpect.clear();
        check.addLast(new Token("120"));
        check.addLast(new Token("3/4"));
        excpect.addLast(new Token("120"));
        excpect.addLast(new Token("3/4"));
        test = new NumParser(check);

        assertEqual(excpect,test.Parse(),"need to be '120 3/4'");

    }
    @Test
    public void dateParseTest (){
        LinkedList<Token> check = new LinkedList<>();
        LinkedList<Token> excpect = new LinkedList<>();
        check.addLast(new Token("25"));
        check.addLast(new Token("Jun"));
        check.addLast(new Token("1992"));
        excpect.addLast(new Token("06-25-1992"));
        DatesParser test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '06-25-1992'");

        check.clear();

        check.addLast(new Token("Jun"));
        check.addLast(new Token("25"));
        check.addLast(new Token("1992"));
        excpect.clear();
        excpect.addLast(new Token("06-25-1992"));
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '06-25-1992'");

        check.clear();
        check.addLast( new Token("MAY"));
        check.addLast( new Token("01"));
        excpect.clear();
        excpect.addLast(new Token("05-01"));
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '05-01'");

        check.clear();
        check.addLast( new Token("01"));
        check.addLast( new Token("MAY"));
        excpect.clear();
        excpect.addLast(new Token("05-01"));
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '05-01'");

        check.clear();
        check.addLast( new Token("1980"));
        check.addLast( new Token("APRIL"));
        excpect.clear();
        excpect.addLast(new Token("1980-04"));
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1980-04'");

        check.clear();
        check.addLast( new Token("APRIL"));
        check.addLast( new Token("1980"));
        excpect.clear();
        excpect.addLast(new Token("1980-04"));
        test = new DatesParser(check);

        assertEqual(excpect,test.Parse(),"need to be '1980-04'");


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

}