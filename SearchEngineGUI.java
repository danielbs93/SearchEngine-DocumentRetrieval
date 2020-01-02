import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.List;

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
    private JButton showDictionaryButton1;
    private String CorpusPath;
    private String PostingPath;
    private boolean Stemmer;
    //    private String DefaultPostingPath = "C:\\Users\\erant\\Desktop\\project\\postingFiles";
    private Manager manager;


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
                    Stemmer = checkBox1.isSelected();
                    File directory=new File(CorpusPath);
                    int fileCount=directory.list().length;
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    manager = new Manager(CorpusPath, PostingPath, checkBox1.isSelected(),fileCount);
                    manager.run();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    fileCount = (new File((PostingPath))).list().length - 2;
                    manager.setCorpusSize(fileCount);
                    manager.SortAndCreate();
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

//                    Path path = Paths.get(PostingPath+"DocIDLexicon.txt");
//                    long lineCount = 0;
//                    try {
//                         lineCount = Files.lines(path).count();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                    File file = new File(PostingPath+"DocIDLexicon.txt");
//                    int docNum = 0;
//                    try {
//                         docNum = (Files.readAllLines(file.toPath())).size();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
                    JOptionPane.showMessageDialog(null, "Num of Docs : "+lines  +"\n"
                            + "Num of Terms : " + manager.getDictionarySize() + "\n"
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
//                    else
//                        PostingPath = DefaultPostingPath;
                    File[] files = (new File(PostingPath)).listFiles();
                    for (File file: files     ) {
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
                JOptionPane.showMessageDialog(null,"Directory:  " + PostingPath + "\n deleted!", "Delete Directory",2);
            }
        });
        /**
         * Upload Dictionary
         */
        showDictionaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(manager!= null) {
                    if (PostingPath.length() == 0)
                        PostingPath = textField2.getText();
                    if (PostingPath.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Please enter path");
                        return;
                    }
//                    manager.SortAndWriteDictionary();

                }
                else
                    JOptionPane.showMessageDialog(null,"Please enter path");
            }
        });
        /**
         * Show Dictionary
         */
        showDictionaryButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                JFrame frame = new JFrame("Dictionary");
//                frame.setBackground(Color.white);
//                frame.getContentPane().setBackground(Color.white);
//                frame.setPreferredSize(new Dimension(850,500));
//                frame.setLocationRelativeTo(null);
//                JCheckBox checkBox1 = new JCheckBox();
//                checkBox1.setBorderPaintedFlat(true);
//                frame.setLocation(700,150);
////                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setState(Frame.ICONIFIED);
//                frame.setState(Frame.NORMAL);
//                frame.pack();
//                frame.setVisible(true);
//                JPanel panel = new JPanel();
//
//                panel.setBackground(Color.WHITE);
////                panel.add(jScrollPane);
//                JTextArea displayOutput = new JTextArea();
//                displayOutput.setBackground(Color.WHITE);
//                PostingPath = "D:\\documents\\users\\erantout\\Downloads\\project\\Gui";
                File file = new File(PostingPath+"\\SortedDictionary.txt");
                if(file.exists()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
////                    BufferedReader reader = null;
//                    try {
////                        reader = new BufferedReader(new FileReader(PostingPath + "\\SortedDictionary.txt"));
//                        List<String> list = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
//                        StringBuilder stringBuilder = new StringBuilder();
//                        for (String s:list) {
//                            stringBuilder.append(s + "\n");
//                        }
//                            displayOutput.append(stringBuilder.toString());
//                    } catch (FileNotFoundException e1) {
//                        e1.printStackTrace();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//                else {
//                    JOptionPane.showMessageDialog(null, "Missing Dictionary");
////                    frame.setDefaultCloseOperation();
//                }
//                JScrollPane jScrollPane = new JScrollPane();
//                jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
////                jScrollPane.setPreferredSize(new Dimension(700, 150));
////                frame.getContentPane().add(jScrollPane, BorderLayout.CENTER);
////                jScrollPane.add(displayOutput);
//                jScrollPane.setBounds(100,100, 50,100);
//                frame.add(jScrollPane);
////                panel.add(displayOutput);
//                frame.add(displayOutput);

//                frame.setSize(800,800);


            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Search-Engine By Daniel and Eran");
        frame.setContentPane(new SearchEngineGUI().panel1);
        frame.setBackground(Color.white);
        frame.getContentPane().setBackground(Color.white);
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
