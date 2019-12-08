package Rules;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class EntitiesParser extends Atext {

    public  EntitiesParser() {
        super();
    }
    public EntitiesParser(String[] s_Array) {
        super(s_Array);
    }

    public EntitiesParser(String doc) {
        super(doc);
    }
    @Override
    public LinkedList<Token> Parse() {
        MaxentTagger maxentTagger = new MaxentTagger("Resources/english-left3words-distsim.tagger");
        String tag = maxentTagger.tagString(this.Document);
        String[] ar_tag = tag.split(" ");
        for (String t: ar_tag) {
            if (t.contains("NNP")) {
                t = t.substring(0,t.indexOf('_'));
                if (t.contains("."))
                    t = t.substring(0,t.indexOf('.'));
                tokenList.add(new Token(t));
            }
        }
        StringBuilder afterTagger = new StringBuilder();
        removeDuplicates();
        for (Token token: tokenList) {
            afterTagger.append(token.getName() + " ");
        }
        Document = afterTagger.toString();
        return tokenList;
    }

    public String getDocAsString() {
        return Document;
    }

}
