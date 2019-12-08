import Rules.EntitiesParser;
import Rules.Stemmer;
import Rules.Token;
import Rules.UpperLowerCaseParser;

import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
//        String sentence = "Alexandria Ocasio-Cortez was walking along Mike and Maria. There was nothing to do unless John gave the order to Eran to call them.";
//        String sentence = "Albanian President Sali Berisha held a meeting with  Ibrahim Rugova, president of the republic of Kosova. First Both presidents assessed as very fruitful and useful first the meeting of Mr. Rugova with President Clinton and the talks he held at the Department of State. They pointed out that these talks are a clear expression of a proper understanding of the necessity to solve the problem of Kosova on the part of President Clinton's  administration. The difficult situation of the Albanians in Kosova and the situation in the region were at the focus of the Berisha-Rugova meeting. Both presidents expressed full support for NATO's  decision on the ultimatum and are of the opinion that the recognition of the Former Yugoslav Republic of Macedonia by the United States contributes to the stability in the south of the Balkans.";
//        String[] s = sentence.split(" ");
//        UpperLowerCaseParser es = new UpperLowerCaseParser(sentence);
//        LinkedList<Token> list = es.Parse();
//        for (Token t: list) {
//            System.out.println(t.getName());
//        }
        Stemmer s = new Stemmer();
        s.add("first");
        System.out.println(s.stem());
        s.clear();
        s.add("First");
        System.out.println(s.stem());
    }
}
