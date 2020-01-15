import Index.IndexManager;
import RetrieveDocuments.MyReader;
import RetrieveDocuments.RetrieveManager;
import javafx.geometry.Pos;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class SearchEngineGUI<Private> {

    private JPanel panel1;
    private JTextField textField2; // posting
    private JTextField textField1; // corpus
    private JCheckBox checkBox1;
    private JButton chooseFileButton1;
    private JButton resetButton;
    private JButton chooseFile;
    private JButton uploadDictionaryButton;
    private JButton showDictionaryButton;
    private JButton showDictionaryButton1;
    private JTextField textField4;
    private JTextField textField5;
    private JButton runQueriesButton;
    private JButton button2;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JButton button1;
    private String CorpusPath;
    private String PostingPath;
    private String query;
    private String QueryPath;
    private boolean stemmer;
    private boolean semantic;
    private boolean entities;

//    private boolean isDelete = false;
    //    private String DefaultPostingPath = "C:\\Users\\erant\\Desktop\\project\\postingFiles";
    private IndexManager indexManager;
    private RetrieveManager retrieveManager;
    private MyReader myReader;
    private HashMap<String, ArrayList<String>> dictionary;


    /**
     * Start Index
     */
    public SearchEngineGUI() {

        uploadDictionaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText().length() == 0 || textField2.getText().length()==0)
                    JOptionPane.showMessageDialog(null,"Please enter path");
                else {
                    CorpusPath = textField1.getText();
                    PostingPath = textField2.getText();
                    stemmer = checkBox1.isSelected();
                    File directory=new File(CorpusPath);
                    int fileCount=directory.list().length;
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    indexManager = new IndexManager(CorpusPath, PostingPath, checkBox1.isSelected(),fileCount);
                    indexManager.run();
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
//                    fileCount = (new File((PostingPath))).list().length - 2;
//                    indexManager.setCorpusSize(fileCount);
//                    indexManager.SortAndCreate();
//                    while (indexManager.isSorting());
                    Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
                    BufferedReader reader = null;
                    int lines = 0;
                    try {
                        reader = new BufferedReader(new FileReader(PostingPath+"\\DocIDLexicon.txt"));
                        while (reader.readLine() != null) lines++;
                        reader.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, "Num of Docs : "+lines  +"\n"
                            + "Num of Terms : " + indexManager.getDictionarySize() + "\n"
                            + "Time for build inverted index : " + (timestamp2.getTime() - timestamp.getTime()) / 1000 + " sec");
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
                fc1.setCurrentDirectory(new java.io.File("C:\\Users\\erant\\Desktop"));
                fc1.setDialogTitle("Choose Directory");
                fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fc1.showOpenDialog(null);
                try {
                    textField1.setText(fc1.getSelectedFile().getAbsolutePath());
                } catch (NullPointerException e1) {}
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
                try {
                    textField2.setText(fc1.getSelectedFile().getAbsolutePath());
                } catch (NullPointerException e1) {}
            }
        });
        /**
         * Browse to Query path
         */
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc1 = new JFileChooser();
                fc1.setCurrentDirectory(new java.io.File("C:\\Users\\erant\\Desktop"));
                fc1.setDialogTitle("Choose Directory");
                fc1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnValue = fc1.showOpenDialog(null);
                try {
                    textField4.setText(fc1.getSelectedFile().getAbsolutePath());
                } catch (NullPointerException e1) {}

            }
        });
        /**
         * Delete Posting File Directory
         */
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField2.getText().length() == 0) {
                    JOptionPane.showMessageDialog(null, "Please insert path to the posting file");
                } else {
                    PostingPath = textField2.getText();
                    File dir = new File(PostingPath);
                    if (dir.isDirectory()) {
                        if (dir.list().length > 0) {
                            int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure yo want to reset");
                            if (dialogResult == JOptionPane.YES_OPTION) {
                                File[] files = (new File(PostingPath)).listFiles();
                                for (File file : files) {
                                    if (file.isDirectory()) {
                                        try {
                                            FileUtils.deleteDirectory(file);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                    file.delete();
                                }
                            }
                            if (dictionary!=null)
                                dictionary.clear();
                            JOptionPane.showMessageDialog(null, "Directory:  " + PostingPath + "\n deleted!", "Delete Directory", 2);
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "Directory already deleted", "Delete Directory", 2);

                        }
                    }
                }
            }
        });
        /**
         * Upload Dictionary
         */
        showDictionaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                if(indexManager != null) {
                    PostingPath = textField2.getText();
                    if (PostingPath.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Please enter path");
                        return;
                    }
                else {
                    try {
                        myReader = new MyReader(PostingPath);
                        dictionary = myReader.loadDictionary();
                        JOptionPane.showMessageDialog(null, "The dictionary uploaded successfully ");
                    }catch (FileNotFoundException e){
                        JOptionPane.showMessageDialog(null, "Sorry, the dictionary has not found ");
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    }
                }
//            }
        });
        /**
         * Show Dictionary
         */
        showDictionaryButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = new File(PostingPath+"\\SortedDictionary.txt");
                if(file.exists()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

/**
 * Run queries
 */
        runQueriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                entities = checkBox3.isSelected();
                semantic = checkBox2.isSelected();
                stemmer = checkBox1.isSelected();
                boolean isPath = false;
                if (dictionary == null)
                    JOptionPane.showMessageDialog(null,"Please upload dictionary");
                else if (textField4.getText().length() == 0 && textField5.getText().length()==0)
                    JOptionPane.showMessageDialog(null,"Please insert path or query");
                else if (textField4.getText().length() != 0 && textField5.getText().length() !=0)
                    JOptionPane.showMessageDialog(null,"Please choose just one option to search");
                else if (textField1.getText().length() == 0)
                    JOptionPane.showMessageDialog(null,"Please enter corpus path");
                else if (textField2.getText().length() == 0)
                    JOptionPane.showMessageDialog(null,"Please enter Posting path path");
                else{
                    CorpusPath = textField1.getText();
                    PostingPath = textField2.getText();
                    if (textField4.getText().length() == 0) // Query from input text
                        query = textField5.getText();
                    else {// Queries from file
                        query = textField4.getText();
                        isPath = true;
                    }
                    retrieveManager = new RetrieveManager(stemmer,semantic,entities,isPath,query,CorpusPath,PostingPath,dictionary);
                    boolean foundDoocuments = retrieveManager.Start();
                    if (!foundDoocuments)
                        JOptionPane.showMessageDialog(null,"Sorry, did not match any documents.");
                    else {
                        int dialogResult = JOptionPane.showConfirmDialog(null, "Show retrieve documents?");
                        if (dialogResult == JOptionPane.YES_OPTION) {
                            File file = new File(PostingPath+"\\RetrievalDocuments.txt");
                            if(file.exists()) {
                                try {
                                    Desktop.getDesktop().open(file);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                    }
                    //DISPLAY
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Search-Engine By Daniel and Eran");
        frame.setContentPane(new SearchEngineGUI().panel1);
        frame.setBackground(Color.white);
        frame.getContentPane().setBackground(Color.white);
        frame.setPreferredSize(new Dimension(850,500));
        frame.setLocationRelativeTo(null);
        JCheckBox checkBox1 = new JCheckBox();
        checkBox1.setBorderPaintedFlat(true);
        frame.setLocation(450,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setState(Frame.ICONIFIED);
        frame.setState(Frame.NORMAL);
        frame.pack();
        frame.setVisible(true);

    }
}
