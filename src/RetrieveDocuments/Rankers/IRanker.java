package RetrieveDocuments.Rankers;
import RetrieveDocuments.AtomicClasses.Document;

import java.util.ArrayList;

public interface IRanker {
    ArrayList<Document> Rank();
}
