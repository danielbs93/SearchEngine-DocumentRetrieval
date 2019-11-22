package Rules;

/**
 * Created by: Daniel Ben-Simon & Eran Toutian
 * the basic form of a string represented in a document.
 */
public class Token {
    private String m_Name;

    public Token() {
        m_Name = "";
    }

    public Token(String m_Name) {
        this.m_Name = m_Name;
    }

    public String getName() {
        return m_Name;
    }

    public void setName(String m_Name) {
        this.m_Name = m_Name;
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

    public boolean isEmpty(){
        return (m_Name == null || m_Name.isEmpty());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Token) && (m_Name.equals(((Token)obj).getName()));
    }
}
