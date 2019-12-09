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
//        Stemmer s = new Stemmer();
//        s.add("Citizens");
//        System.out.println(s.stem());
//        s.clear();
//        s.add("First");
//        System.out.println(s.stem());
        String doc = "<TEXT> French-language Algiers daily EL WATAN of 10 and 23 January has " +
                "reported that Algerian Tuareg frustration and anger is growing in " +
                "Tamanrasset because of the high number of illegal foreigners (mainly " +
                "from Niger, Mali, and Ghana) who reportedly outnumber Algerians in " +
                "the region.  The Tuaregs hold these foreigners responsible for " +
                "increasing crime rates, attacks against tourists, and drug " +
                "trafficking which weakens the economy and creates a security problem " +
                "in the region.  Hadj Moussa Akhmokh, a prominent Tuareg authority, " +
                "says a 1992 plan to improve regional security was unsuccessful and " +
                "that \"the situation has become worse.\"  In December, for example, " +
                "the two national firms National Company for Mining Research " +
                "(SONAREM) and Center for Energy Research (CREM) were looted and " +
                "several vehicles were stolen. In addition, customs officials from " +
                "Tamanrasset were kidnapped by a group of individuals armed with " +
                "Kalashnikovs, robbed, and taken to Niger where they were manhandled " +
                "before being released. According to Hadj Akhamokh, the more recent " +
                "kidnapping of 18 Tuaregs (10 Algerians and eight Malians) set a " +
                "\"serious\" precedent and was the " +
                "\"last straw.\" Citizens planned a peaceful march to demonstrate their " +
                "anger and to attract the attention of authorities until Hadj " +
                "Akhamokh convinced them to postpone the march.  Hadj Akhmokh stated " +
                "the Tuaregs have responded to his appeal, but that they are " +
                "\"impatient,\" and are waiting for the government to take control of " +
                "the situation. </TEXT>";
        Parser p = new Parser(doc,true);
        LinkedList<Token>[] parsed = p.Parse();
        int s1 = Math.max(parsed[0].size(),parsed[1].size());
//        s1 = Math.max(s1,parsed[2].size());
        System.out.println("term     | Entity + UpperLower ");
        System.out.println("-------------------------------------");
        for (int i = 0; i < s1; i++) {
            if (i <parsed[0].size())
                System.out.print(parsed[0].get(i).getName() + "         ");
            if (i <parsed[1].size())
                System.out.print(parsed[1].get(i).getName() + "         ");
//            if (i <parsed[2].size())
//                System.out.print(parsed[2].get(i).getName() + "         ");
            System.out.println();
        }
    }
}
