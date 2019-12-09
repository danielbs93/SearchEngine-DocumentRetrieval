import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchEngineGUI<Private> {

    private JPanel panel1;
    private JTextField textField2;
    private JTextField textField1;
    private JCheckBox checkBox1;
    private JButton chooseFileButton1;
    private JButton resetButton;
    private JButton chooseFile;
    private JButton uploadDictionaryButton;
    private JButton showDictionaryButton;
    private String CorpusPath;
    private String PostingPath;
    private boolean Stemmer;



    /**
     * upload the dictionary to the memory
     */
    public SearchEngineGUI() {


        uploadDictionaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText().length() == 0 || textField2.getText().length()==0)
                    JOptionPane.showMessageDialog(null,"Please enter path");
                else{
                    CorpusPath = textField1.getText();
                    PostingPath = textField2.getText();
                    Stemmer = checkBox1.isSelected();
                }
            }
        });
        /**
         * Browse to corpus path
         */
        chooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc1 = new JFileChooser();
                fc1.setCurrentDirectory(new java.io.File("C:/"));
                fc1.setDialogTitle("Choose Directory");
                fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fc1.showOpenDialog(null);
                textField1.setText(fc1.getSelectedFile().getAbsolutePath());
            }
        });
        /**
         *Browse to posting path
         */
        chooseFileButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc1 = new JFileChooser();
                fc1.setCurrentDirectory(new java.io.File("C:/"));
                fc1.setDialogTitle("Choose Directory");
                fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fc1.showOpenDialog(null);
                textField2.setText(fc1.getSelectedFile().getAbsolutePath());
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog (null,"Are you sure yo want to reset");
                if(dialogResult == JOptionPane.YES_OPTION){

                    JOptionPane.showMessageDialog(null,PostingPath);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Search-Engine By Eran and Daniel");
        frame.setContentPane(new SearchEngineGUI().panel1);
        frame.setBackground(Color.white);
        frame.getContentPane().setBackground(Color.white);
//        frame.setSize(400,400);
        frame.setPreferredSize(new Dimension(700,500));
        frame.setLocationRelativeTo(null);
        JCheckBox checkBox1 = new JCheckBox();
        checkBox1.setBorderPaintedFlat(true);
        frame.setLocation(400,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setState(Frame.ICONIFIED);
        frame.setState(Frame.NORMAL);
        frame.pack();
        frame.setVisible(true);
    }
}
