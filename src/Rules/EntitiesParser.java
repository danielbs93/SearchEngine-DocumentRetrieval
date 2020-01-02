package Rules;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.ArrayList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class EntitiesParser extends Atext {
    private MaxentTagger maxentTagger;
    public  EntitiesParser(ArrayList<Token> tokenlist, MaxentTagger maxentTagger) {
        super(tokenlist);
        this.maxentTagger = maxentTagger;
    }
    public EntitiesParser(String[] s_Array) {
        super(s_Array);
    }

    public EntitiesParser(String doc) {
        super(doc);
    }

    public ArrayList<Token> Parse() {
        ArrayList<Token> Result = new ArrayList<>();
        String tag = "";
        try {
            tag = maxentTagger.tagString(this.Document.toString());
        }catch (Exception e) {
            //nothing
        }
        String[] ar_tag = tag.split(" ");
        StringBuilder name = new StringBuilder();
        for (String t: ar_tag) {
            if (t.contains("NNP")) {
                name.append(t, 0, t.indexOf('_'));
//                if (t.contains("."))
//                    t = t.substring(0,t.indexOf('.'));
                if (name.length() != 0 && name.toString() != "")
                    Result.add(new Token(name.toString(),true));
            }
            else {
                name.append(t,0,t.lastIndexOf('_'));
                if (name.length() != 0 && name.toString() != "")
                    Result.add(new Token(name.toString()));
            }
            name.setLength(0);
        }

        StringBuilder afterTagger = new StringBuilder();
        for (Token token: Result) {
            if (token != null) {
                afterTagger.append(token.getName() + " ");
                for (Token forPosition : tokenList) {
                    if (forPosition != null && token.equals(forPosition)) {
                        token.addPosition(forPosition.getPosition());
//                    token.increaseTF();
                    }
                }
            }
        }
        //removeDuplicates(Result);
        Document = new StringBuilder(afterTagger.toString());
        return Result;
    }

    public StringBuilder getDocAsString() {
        return Document;
    }

}
