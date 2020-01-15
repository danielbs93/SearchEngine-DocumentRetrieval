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
    private int isEntity;


    public Token() {
        m_Name = "";
        position = -1;
        tf = 1;
        positions = new StringBuilder();
        isEntity = 0;
    }

    public Token(String m_Name, int position) {
        this.m_Name = m_Name;
        this.position = position;
        tf = 1;
        positions = new StringBuilder();
        positions.append(position);
        isEntity = 0;
    }

    public Token(String m_Name, int position,boolean entitiy) {
        this.m_Name = m_Name;
        this.position = position;
        tf = 1;
        positions = new StringBuilder();
        positions.append(position);
        if (entitiy)
            isEntity = 1;
        else
            isEntity = 0;
    }

    public Token(String m_Name) {
        this.m_Name = m_Name;
        tf = 1;
        this.position = 0;
        positions = new StringBuilder();
        isEntity = 0;
    }

    public Token(Token t) {
        tf = t.tf;
        this.m_Name = t.getName();
        this.position = t.position;
        this.positions = new StringBuilder(t.positions);
        isEntity = t.isEntity;
    }

    public Token(String name, boolean entity) {
        tf = 1;
        this.m_Name = name;
        this.position = -1;
        positions = new StringBuilder();
        if (entity)
            this.isEntity = 1;
        else
            isEntity = 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        this.positions.setLength(0);
        this.positions.append(position);
    }


    public int isEntity() {
        return isEntity;
    }

    public void setEntity(boolean entity) {
        if (entity)
            isEntity = 1;
        else
            isEntity = 0;
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

    public void addPosition(int position) {
        if (this.position < 0)
            this.position = 0;
        positions.append("," + position);
    }

    public void sortPositionsByGaps() {
        if (positions.length() != 0 && positions.toString().contains(",")) {
            String[] pos = positions.toString().split(",");
            positions.setLength(0);
            int previous = 0, current_pos = 0;
            for (int i = 0; i < pos.length; i++) {
                try {
                    current_pos = Integer.parseInt(pos[i]);
                } catch (NumberFormatException e) {
                    continue;
                }
                if (i == 0) {
                    positions.append(current_pos);
                } else {
                    positions.append("," + Math.abs(current_pos - previous));
                }
                previous = current_pos;
            }
        }
    }


    /**
     * @return true if m_Name is numeric or not
     */
    public boolean isNumeric() {
        try {
            Double d = Double.parseDouble(m_Name.replaceAll(",",""));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    /**
     * @return true if the token is legal fraction
     */
    public boolean isFraction() {
        if (this.getName().contains("/")) {
            int index = this.getName().indexOf("/");
            String num1 = this.getName().substring(0, index);
            String num2 = this.getName().substring(index + 1);
            try {
                Double.parseDouble(num1);
            } catch (NumberFormatException e) {
                return false;
            }
            try {
                Double.parseDouble(num2);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return (m_Name == null || m_Name.isEmpty());
    }

    public boolean isEqual(Token token) {
        return m_Name.equals(token.getName()) && this.position == token.position;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Token) && (m_Name.equals(((Token) obj).getName()));
    }

    @Override
    public int hashCode() {
        return m_Name == null ? 0 : m_Name.hashCode();
    }


}