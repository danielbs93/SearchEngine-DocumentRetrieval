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
    @Override
    public ArrayList<Token> Parse() {
        ArrayList<Token> Result = new ArrayList<>();
        String tag = maxentTagger.tagString(this.Document.toString());
        String[] ar_tag = tag.split(" ");
        for (String t: ar_tag) {
            if (t.contains("NNP")) {
                t = t.substring(0,t.indexOf('_'));
                if (t.contains("."))
                    t = t.substring(0,t.indexOf('.'));
                Result.add(new Token(t));
            }
        }

        StringBuilder afterTagger = new StringBuilder();
        for (Token token: Result) {
            afterTagger.append(token.getName() + " ");
            for (Token forPosition:tokenList) {
                if (token.equals(forPosition)) {
                    token.addPosition(forPosition.getPosition());
                    token.increaseTF();
                }
            }
        }
        removeDuplicates(Result);
        Document = new StringBuilder(afterTagger.toString());
        return Result;
    }

    public String getDocAsString() {
        return Document.toString();
    }

}
