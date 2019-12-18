import Rules.Token;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
    private String DefaultPostingPath = "C:\\Users\\erant\\Desktop\\project\\postingFiles";



    /**
     * upload the dictionary to the memory
     */
    public SearchEngineGUI() {


        uploadDictionaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if (textField1.getText().length() == 0 || textField2.getText().length()==0)
//                    JOptionPane.showMessageDialog(null,"Please enter path");
//                else{
//                    CorpusPath = textField1.getText();
//                    PostingPath = textField2.getText();
//                    Stemmer = checkBox1.isSelected();
////                    Indexer indexer = new Indexer(CorpusPath,PostingPath,Stemmer);
////                    indexer.Index();
////                    JProgressBar jpb = new JProgressBar(0,100);
////                    jpb.setValue(0);
////                    jpb.setStringPainted(true);
//                }
                String CorpusPath = "C:\\Users\\erant\\Desktop\\project\\corpus";
                String CorpusPath2 = "C:\\Users\\erant\\Desktop\\corpus";
                String savingPath = "C:\\Users\\erant\\Desktop\\project\\postingFiles";
                String savingPath2 = "C:\\Users\\erant\\Desktop\\project\\postingFiles2";
                AtomicInteger DocID = new AtomicInteger(0);
                AtomicInteger TermID = new AtomicInteger(0);
                AtomicInteger FileID = new AtomicInteger(0);
                ConcurrentHashMap<Token, MutablePair<Integer, Integer>> Dictionary = new ConcurrentHashMap<>();
                ConcurrentHashMap<Token, ArrayList<Integer>> EntitiesDictionary = new ConcurrentHashMap<>();
                //Indexer indexer = new Indexer(Dictionary,EntitiesDictionary,CorpusPath,savingPath,true,DocID,TermID,FileID);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                indexer.Index();
//                Indexer indexer2 = new Indexer(Dictionary,EntitiesDictionary,CorpusPath2,savingPath2,true,DocID,TermID,FileID);
//                indexer2.Index();
                Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
                System.out.println(timestamp2.getTime()-timestamp.getTime());
            }
        });
        /**
         * Browse to corpus path
         */
        chooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc1 = new JFileChooser();
                fc1.setCurrentDirectory(new java.io.File("C:\\Users\\erant\\Desktop"));
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
                fc1.setCurrentDirectory(new java.io.File("C:\\Users\\erant\\Desktop"));
                fc1.setDialogTitle("Choose Directory");
                fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fc1.showOpenDialog(null);
                textField2.setText(fc1.getSelectedFile().getAbsolutePath());
            }
        });
        /**
         * Delete Posting File Directory
         */
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog (null,"Are you sure yo want to reset");
                if(dialogResult == JOptionPane.YES_OPTION){
                    if(textField2.getText().length() != 0)
                        PostingPath = textField2.getText();
                    else
                        PostingPath = DefaultPostingPath;
                    File[] files = (new File(PostingPath)).listFiles();
                    for (File file: files
                         ) { file.delete(); }
                }
                JOptionPane.showMessageDialog(null,"Directory:  " + PostingPath + "\n deleted!", "Delete Directory",2);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Search-Engine By Daniel and Eran");
        frame.setContentPane(new SearchEngineGUI().panel1);
        frame.setBackground(Color.white);
        frame.getContentPane().setBackground(Color.white);
        JButton chooseFile = new JButton();
//        chooseFile.setPreferredSize(new Dimension(3,2));
//        frame.add(chooseFile);
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
        String s = "5,35";
        String [] ss= s.split(",");
        System.out.println(ss[ss.length]);
    }
}
