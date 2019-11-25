package Rules;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.LinkedList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class EntitiesParser extends Atext {

    public EntitiesParser(String[] s_Array) {
        super(s_Array);
        for (String s: s_Array) {
            Document = Document + s + " ";
        }
        Document = Document.substring(0,Document.length() - 1);
    }

    public EntitiesParser(String doc) {
        super(doc);
        s_Array = doc.split(" ");
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

        removeDuplicates();
        return tokenList;
    }

}
