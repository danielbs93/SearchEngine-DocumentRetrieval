package RetrieveDocuments.AtomicClasses;

public class Term {

    private int termID;
    private int tf;
    private int isEntity;
    private int[] positions;
    private int df;
    private String termName;

    //constructor for term from posting file
    public Term(String[] Data, int df, String name) {
        termID = Integer.parseInt(Data[0]);
        tf = Integer.parseInt(Data[2]);
        isEntity = Integer.parseInt(Data[3]);
        this.df= df;
        termName = name;
        String[] pos = Data[4].split(",");
        positions = new int[pos.length];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = Integer.parseInt(pos[i]);
        }
    }

    //constructor for term from query
    public Term(String termFromQuery, int termId) {
        termName = termFromQuery;
        termID = termId;
        df = -1;
        positions = null;
        isEntity = -1;
        tf = -1;
    }

    public String getTermName() {
        return termName;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public int getIsEntity() {
        return isEntity;
    }

    public int[] getPositions() {
        return positions;
    }

    /**
     * this function sets position only for queries terms
     * @param positions
     */
    public void setQueryTermPositions(int positions) {
        this.positions = new int[1];
        this.positions[0] = positions;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Term) && termID == ((Term)obj).termID;
    }
}
