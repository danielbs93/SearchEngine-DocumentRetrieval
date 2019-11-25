import Rules.DatesParser;
import Rules.Token;
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
    public void dateParseTest (){
        LinkedList<Token> check = new LinkedList<>();
        LinkedList<Token> excpect = new LinkedList<>();
        check.addLast(new Token("25"));
        check.addLast(new Token("Jun"));
        check.addLast(new Token("1992"));
        excpect.addLast(new Token("06-25-1992"));
        DatesParser test = new DatesParser(check);

        assertEquals(excpect.getFirst().getName(),test.Parse().getFirst().getName(),"need to be '06-25-1992'");

        check.clear();
        check.addLast(new Token("Jun"));
        check.addLast(new Token("25"));
        check.addLast(new Token("1992"));
        excpect.clear();
        excpect.addLast(new Token("06-25-1992"));
        test = new DatesParser(check);

        assertEquals(excpect.getFirst().getName(),test.Parse().getFirst().getName(),"need to be '06-25-1992'");

        check.clear();
        check.addLast( new Token("MAY"));
        check.addLast( new Token("01"));
        excpect.clear();
        excpect.addLast(new Token("05-01"));
        test = new DatesParser(check);

        assertEquals(excpect.getFirst().getName(),test.Parse().getFirst().getName(),"need to be '05-01'");

        check.clear();
        check.addLast( new Token("01"));
        check.addLast( new Token("MAY"));
        excpect.clear();
        excpect.addLast(new Token("05-01"));
        test = new DatesParser(check);

        assertEquals(excpect.getFirst().getName(),test.Parse().getFirst().getName(),"need to be '05-01'");

        check.clear();
        check.addLast( new Token("1980"));
        check.addLast( new Token("APRIL"));
        excpect.clear();
        excpect.addLast(new Token("1980-04"));
        test = new DatesParser(check);

        assertEquals(excpect.getFirst().getName(),test.Parse().getFirst().getName(),"need to be '1980-04'");

        check.clear();
        check.addLast( new Token("APRIL"));
        check.addLast( new Token("1980"));
        excpect.clear();
        excpect.addLast(new Token("1980-04"));
        test = new DatesParser(check);

        assertEquals(excpect.getFirst().getName(),test.Parse().getFirst().getName(),"need to be '1980-04'");


    }

}
