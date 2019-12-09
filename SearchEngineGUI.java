import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchEngineGUI {

    private JPanel panel1;
    private JTextField textField2;
    private JTextField textField1;
    private JCheckBox checkBox1;
    private JButton chooseFileButton1;
    private JButton showDictoneryButton;
    private JButton resetButton;
    private JButton chooseFile;
    private JButton uploadDictoneryButton;

    public SearchEngineGUI() {
        chooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Search-Engine By Eran and Daniel");
        frame.setContentPane(new SearchEngineGUI().panel1);
        frame.setBackground(Color.white);
//        frame.setSize(400,400);
        frame.setPreferredSize(new Dimension(700,500));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton chooseFileButton = new JButton();
        JButton chooseFile = new JButton();
        JFileChooser fc1 = new JFileChooser();
        fc1.setCurrentDirectory(new java.io.File("C:/"));
        fc1.setDialogTitle("Choose Directory");
        fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        System.out.println(fc1.getSelectedFile().getAbsolutePath());
        JCheckBox checkBox1 = new JCheckBox();
        frame.pack();
        frame.setVisible(true);
    }
}
