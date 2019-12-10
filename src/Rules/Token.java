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
    }

    public Token(String m_Name, int position) {
        this.m_Name = m_Name;
        this.position = position;
        tf = 1;
        positions.append(position);
    }

    public Token(String m_Name) {
        this.m_Name = m_Name;
        this.position=-1;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Token(Token t) {this.m_Name = t.getName();}

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

    public void addPosition(int position){
        int gap = position - this.position;
        positions.append("," + gap);
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

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Token) && (m_Name.equals(((Token)obj).getName()) && this.position == ((Token)obj).position);
    }
}