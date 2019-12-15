package Rules;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 * the basic form of a string represented in a document.
 */
public class Token {

    private String m_Name;
    private int position;
    private int tf;
    private StringBuilder positions;


    public Token() {
        m_Name = "";
        position = -1;
        tf = 1;
        positions = new StringBuilder();
    }

    public Token(String m_Name, int position) {
        this.m_Name = m_Name;
        this.position = position;
        tf = 1;
        positions = new StringBuilder();
        positions.append(position);
    }

    public Token(String m_Name) {
        this.m_Name = m_Name;
        tf = 1;
        this.position=-1;
        positions = new StringBuilder();
    }

    public Token(Token t) {
        tf = t.tf;
        this.m_Name = t.getName();
        this.position = t.position;
        this.positions = new StringBuilder(t.positions);
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        this.positions.append(position);
    }


    public String getName() {
        return m_Name;
    }

    public void setName(String m_Name) {
        this.m_Name = m_Name;
    }

    public int getTf() {
        return tf;
    }

    public void increaseTF() {
        tf++;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public String getPositions() {
        return this.positions.toString();
    }

    public void addPosition(int position){
        int gap = Math.abs(position - this.position);
        positions.append("," + gap);
        this.position = gap;
    }


    /**
     *
     * @return true if m_Name is numeric or not
     */
    public boolean isNumeric() {
        try {
            Double.parseDouble(m_Name);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


    /**
     *
     * @return true if the token is legal fraction
     */
    public boolean isFraction(){
        if (this.getName().contains("/")){
            int index = this.getName().indexOf("/");
            String num1 = this.getName().substring(0,index);
            String num2 = this.getName().substring(index+1);
            try {
                Double.parseDouble(num1);
            } catch(NumberFormatException e){
                return false;
            }
            try {
                Double.parseDouble(num2);
            } catch(NumberFormatException e){
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isEmpty(){
        return (m_Name == null || m_Name.isEmpty());
    }

    public boolean isEqual(Token token) {
        return m_Name.equals(token.getName()) && this.position == token.position;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Token) && (m_Name.equals(((Token)obj).getName()));
    }

    @Override
    public int hashCode() {
        return m_Name.hashCode();
    }
}