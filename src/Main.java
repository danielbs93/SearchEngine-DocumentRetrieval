import Rules.EntitiesParser;
import Rules.Token;

import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        String sentence = "Alexandria Ocasio-Cortez was walking along Maria. There was nothing to do unless John gave the order to Eran to call them.";
        String[] s = sentence.split(" ");
        EntitiesParser es = new EntitiesParser(s);
        LinkedList<Token> list = es.Parse();
        for (Token t: list) {
            System.out.println(t.getName());
        }
    }
}
