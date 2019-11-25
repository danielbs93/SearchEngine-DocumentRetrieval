package Rules;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class EntitiesParser extends Atext {

    private InputStream in;
    private TokenNameFinderModel model;
    private NameFinderME nameFinder;

    public EntitiesParser(String[] s_Array) {
        super(s_Array);
        try {
            in = new FileInputStream("C:/Users/USER/Desktop/java files/Search Engine - Document Retrieval/Resources/en-ner-person.bin");
            model = new TokenNameFinderModel(in);
            nameFinder = new NameFinderME(model);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public LinkedList<Token> Parse() {
        Span namesSpan[] = nameFinder.find(s_Array);
        for (Span s: namesSpan) {
            if (s.getType().equals("person")) {
                String name = s_Array[s.getStart()];
                tokenList.add(new Token(name));
            }
        }
        return tokenList;
    }

//    public static void main(String args[][]) {
//        String sentence = "Alexandria Ocasio-Cortez was walking along Maria. There was nothing to do unless John gave the order to Eran to call them.";
//        String[] s = sentence.split(" ");
//        EntitiesParser es = new EntitiesParser(s);
//        LinkedList<Token> list = es.Parse();
//        for (Token t: list) {
//            System.out.println(t.getName());
//        }
//
//    }
}
