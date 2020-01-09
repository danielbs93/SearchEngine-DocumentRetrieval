package RetrieveDocuments;

import Index.Parser;

public class RetrieveManager {

    private Searcher mySearcher;
    private Parser parser;
    private boolean isStemmer;
    private boolean isSemantic;
    private boolean isDominantEntities;
    private String savingPath;
    private IdentifyDominantEntities dominantEntities;

    public RetrieveManager(boolean isStemmer, boolean isSemantic,boolean dominantEntities, String savingPath) {
        this.isStemmer = isStemmer;
        this.isSemantic = isSemantic;
        this.savingPath = savingPath;
        isDominantEntities = dominantEntities;
    }
}
