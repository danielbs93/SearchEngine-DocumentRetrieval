package Rules;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.ArrayList;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 */
public class EntitiesParser extends Atext {
    private MaxentTagger maxentTagger;
    public  EntitiesParser(String[] tokenlist, MaxentTagger maxentTagger) {
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
//        String modelFile = "Resources/english-left3words-distsim.tagger";
//        MaxentTagger maxentTagger = new MaxentTagger(modelFile, StringUtils.argsToProperties(new String[]{"-model", modelFile}),false);
        String tag = maxentTagger.tagString(this.Document.toString());
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
        for (Token token: tokenList) {
            afterTagger.append(token.getName() + " ");
        }
        Document = new StringBuilder(afterTagger.toString());
        return tokenList;
    }

    public String getDocAsString() {
        return Document.toString();
    }

}
