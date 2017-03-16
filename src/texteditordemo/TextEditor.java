/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texteditordemo;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;

/**
 *
 * @author Keyur
 */
public class TextEditor extends JFrame implements ActionListener, ClipboardOwner, ComponentListener {
    
    JPopupMenu rightClickMenu;
    JMenuBar menuBar;
    JTextArea textArea;
    JMenuItem newItem;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem saveAsItem;
    JMenuItem exitItem;
    JMenuItem undoItem;
    JMenuItem redoItem;
    JMenuItem cutItem;
    JMenuItem copyItem;
    JMenuItem pasteItem;
    JMenuItem deleteItem;
    JMenuItem selectAllItem;
    JMenuItem dateAndTimeItem;
    JMenuItem fontItem;
    JMenuItem sizeItem;
    JMenuItem foregroundColorItem;
    JMenuItem backgroundColorItem;
    JMenuItem popupUndoItem;
    JMenuItem popupRedoItem;
    JMenuItem popupCutItem;
    JMenuItem popupCopyItem;
    JMenuItem popupPasteItem;
    JMenuItem popupDeleteItem;
    JMenuItem popupSelectAllItem;
    JMenuItem titleCaseItem;
    JMenuItem upperCaseItem;
    JMenuItem lowerCaseItem;
    JMenuItem aboutItem;
    JCheckBoxMenuItem wrapItem;
    JRadioButtonMenuItem boldTextItem;
    JRadioButtonMenuItem italicTextItem;
    JRadioButtonMenuItem plainTextItem;
    File openFile;
    File saveFile;
    int selectedLength;
    JFileChooser fileChooser = new JFileChooser();
    UndoManager undoManager = new UndoManager();
    Rectangle window;
    String title = "Untitled";
    JScrollPane textAreaScrollPane;
    

    public TextEditor() throws HeadlessException {
        setTitle(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(openFile == null && textArea.getText().equals("")) {
                    System.exit(0);
                }
                else {
                    int returnValue = JOptionPane.showConfirmDialog(TextEditor.this, "Do you want to save the current changes?", "Confirm Save", JOptionPane.YES_NO_CANCEL_OPTION);
                    if(returnValue == JOptionPane.YES_OPTION) {
                        if(openFile == null) {
                            int anotherReturnValue = fileChooser.showSaveDialog(TextEditor.this);
                            if(anotherReturnValue == JFileChooser.APPROVE_OPTION) {
                                saveFile = fileChooser.getSelectedFile();
                            }
                        }
                        else {
                            saveFile = openFile;
                        }
                        FileWriter fw;
                        try {
                            fw = new FileWriter(saveFile);
                            BufferedWriter bw = new BufferedWriter(fw);
                            bw.write(textArea.getText());
                            bw.flush();
                            bw.close();
                        } catch (IOException ex) {
                            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.exit(0);
                    }
                    else if(returnValue == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    }
                }
            }
        });
        setSize(600, 600);
        addComponentListener(this);
        window = getBounds();
        setLayout(null);
        add(makeMenu());
        
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        textArea.setWrapStyleWord(true);
        textArea.addCaretListener(new CaretListener() {

            @Override
            public void caretUpdate(CaretEvent e) {
                selectedLength = textArea.getSelectionEnd()-textArea.getSelectionStart();
                if(selectedLength != 0) {
                    cutItem.setEnabled(true);
                    copyItem.setEnabled(true);
                    deleteItem.setEnabled(true);
                    titleCaseItem.setEnabled(true);
                    upperCaseItem.setEnabled(true);
                    lowerCaseItem.setEnabled(true);
                    popupCutItem.setEnabled(true);
                    popupCopyItem.setEnabled(true);
                    popupDeleteItem.setEnabled(true);
                }
            }
        });
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {

            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
                undoItem.setEnabled(true);
                popupUndoItem.setEnabled(true);
            }
        });
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!textArea.getText().equals("")) {
                    saveItem.setEnabled(true);
                    saveAsItem.setEnabled(true);
                    selectAllItem.setEnabled(true);
                    popupSelectAllItem.setEnabled(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(textArea.getText().equals("")) {
                    saveItem.setEnabled(false);
                    saveAsItem.setEnabled(false);
                    selectAllItem.setEnabled(false);
                    popupSelectAllItem.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                
            }
        });
        textArea.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        textAreaScrollPane = new JScrollPane(textArea);
        //textArea.setBounds(0, 30, window.width - 6, window.height - 30 - 29);
        textAreaScrollPane.setBounds(0, 30, window.width - 16, window.height - 30 - 39);
        add(textAreaScrollPane);
        
    }
    
    private JMenuBar makeMenu() {
        menuBar = new JMenuBar();
        rightClickMenu = new JPopupMenu();
        menuBar.setBounds(0, 0, window.width, 30);
        
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
        JMenu formatMenu = new JMenu("Format");
        menuBar.add(formatMenu);
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        saveAsItem = new JMenuItem("Save As");
        exitItem = new JMenuItem("Exit", KeyEvent.VK_ESCAPE);
        
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        saveAsItem.addActionListener(this);
        exitItem.addActionListener(this);
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        saveItem.setEnabled(false);
        saveAsItem.setEnabled(false);
        
        undoItem = new JMenuItem("Undo", 'Z');
        redoItem = new JMenuItem("Redo", 'Y');
        cutItem = new JMenuItem("Cut", 'X');
        copyItem = new JMenuItem("Copy", 'C');
        pasteItem = new JMenuItem("Paste", 'V');
        deleteItem = new JMenuItem("Delete", KeyEvent.VK_DELETE);
        selectAllItem = new JMenuItem("Select All", 'A');
        dateAndTimeItem = new JMenuItem("Date & Time");
        
        undoItem.addActionListener(this);
        redoItem.addActionListener(this);
        cutItem.addActionListener(this);
        copyItem.addActionListener(this);
        pasteItem.addActionListener(this);
        deleteItem.addActionListener(this);
        selectAllItem.addActionListener(this);
        dateAndTimeItem.addActionListener(this);
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(deleteItem);
        editMenu.add(selectAllItem);
        editMenu.addSeparator();
        editMenu.add(dateAndTimeItem);
        
        undoItem.setEnabled(false);
        redoItem.setEnabled(false);
        selectAllItem.setEnabled(false);
        cutItem.setEnabled(false);
        copyItem.setEnabled(false);
        deleteItem.setEnabled(false);
        
        fontItem = new JMenuItem("Font", 'F');
        sizeItem = new JMenuItem("Size");
        foregroundColorItem = new JMenuItem("Text Color");
        backgroundColorItem = new JMenuItem("Background Color");
        wrapItem = new JCheckBoxMenuItem("Text Wrap");
        wrapItem.setSelected(true);
        
        fontItem.addActionListener(this);
        sizeItem.addActionListener(this);
        foregroundColorItem.addActionListener(this);
        backgroundColorItem.addActionListener(this);
        wrapItem.addActionListener(this);
        
        JMenu caseTweakingSubMenu = new JMenu("Case Tweaking");
        JMenu decorationSubMenu = new JMenu("Decoration");
        
        titleCaseItem = new JMenuItem("Title Case");
        upperCaseItem = new JMenuItem("Upper Case");
        lowerCaseItem = new JMenuItem("Lower Case");
        boldTextItem = new JRadioButtonMenuItem("Bold Text");
        italicTextItem = new JRadioButtonMenuItem("Italic Text");
        plainTextItem = new JRadioButtonMenuItem("Plain Text");
        
        ButtonGroup decorationButtons = new ButtonGroup();
        decorationButtons.add(boldTextItem);
        decorationButtons.add(italicTextItem);
        decorationButtons.add(plainTextItem);
                
        titleCaseItem.addActionListener(this);
        upperCaseItem.addActionListener(this);
        lowerCaseItem.addActionListener(this);
        boldTextItem.addActionListener(this);
        italicTextItem.addActionListener(this);
        plainTextItem.addActionListener(this);
        
        caseTweakingSubMenu.add(titleCaseItem);
        caseTweakingSubMenu.add(upperCaseItem);
        caseTweakingSubMenu.add(lowerCaseItem);
        decorationSubMenu.add(boldTextItem);
        decorationSubMenu.add(italicTextItem);
        decorationSubMenu.add(plainTextItem);
        
        plainTextItem.setSelected(true);
        titleCaseItem.setEnabled(false);
        upperCaseItem.setEnabled(false);
        lowerCaseItem.setEnabled(false);
        
        formatMenu.add(fontItem);
        formatMenu.add(sizeItem);
        formatMenu.add(foregroundColorItem);
        formatMenu.add(backgroundColorItem);
        formatMenu.addSeparator();
        formatMenu.add(caseTweakingSubMenu);
        formatMenu.add(decorationSubMenu);
        formatMenu.add(wrapItem);
        
        popupUndoItem = new JMenuItem("Undo");
        popupRedoItem = new JMenuItem("Redo");
        popupCutItem = new JMenuItem("Cut");
        popupCopyItem = new JMenuItem("Copy");
        popupPasteItem = new JMenuItem("Paste");
        popupDeleteItem = new JMenuItem("Delete");
        popupSelectAllItem = new JMenuItem("Select All");
        
        popupCopyItem.addActionListener(this);
        popupCutItem.addActionListener(this);
        popupDeleteItem.addActionListener(this);
        popupPasteItem.addActionListener(this);
        popupSelectAllItem.addActionListener(this);
        popupRedoItem.addActionListener(this);
        popupUndoItem.addActionListener(this);
        
        rightClickMenu.add(popupUndoItem);
        rightClickMenu.add(popupRedoItem);
        rightClickMenu.addSeparator();
        rightClickMenu.add(popupCutItem);
        rightClickMenu.add(popupCopyItem);
        rightClickMenu.add(popupPasteItem);
        rightClickMenu.add(popupDeleteItem);
        rightClickMenu.addSeparator();
        rightClickMenu.add(popupSelectAllItem);
        
        popupUndoItem.setEnabled(false);
        popupRedoItem.setEnabled(false);
        popupCutItem.setEnabled(false);
        popupCopyItem.setEnabled(false);
        popupDeleteItem.setEnabled(false);
        popupSelectAllItem.setEnabled(false);
        
        aboutItem = new JMenuItem("About TextEditor");
        
        aboutItem.addActionListener(this);
        
        helpMenu.add(aboutItem);
        
        return menuBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Copy")) {
            if(selectedLength != 0) {
                StringSelection selectedString = new StringSelection(textArea.getSelectedText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selectedString , this);
            }
        }
        else if(e.getActionCommand().equals("Exit")) {
            if(openFile == null && textArea.getText().equals("")) {
                System.exit(0);
            }
            else {
                int returnValue = JOptionPane.showConfirmDialog(this, "Do you want to save the current changes?", "Confirm Save", JOptionPane.YES_NO_CANCEL_OPTION);
                if(returnValue == JOptionPane.YES_OPTION) {
                    if(openFile == null) {
                        int anotherReturnValue = fileChooser.showSaveDialog(this);
                        if(anotherReturnValue == JFileChooser.APPROVE_OPTION) {
                            saveFile = fileChooser.getSelectedFile();
                        }
                    }
                    else {
                        saveFile = openFile;
                    }
                    FileWriter fw;
                    try {
                        fw = new FileWriter(saveFile);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(textArea.getText());
                        bw.flush();
                        bw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.exit(0);
                }
                else if(returnValue == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        }
        else if(e.getActionCommand().equals("Paste")) {
            String pasteString = "";
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            boolean isTransferrable = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
            if(isTransferrable) {
                try {
                    pasteString = (String) contents.getTransferData(DataFlavor.stringFlavor);
                }
                catch (UnsupportedFlavorException ex) {
                    System.out.println(ex);
                }
                catch (IOException ex) {
                    System.out.println(ex);
                }
                try {
                    String preFix = textArea.getText(0, textArea.getCaretPosition());
                    String sufFix = textArea.getText(textArea.getCaretPosition(), textArea.getText().length() - textArea.getCaretPosition());
                    textArea.setText(preFix + pasteString + sufFix);
                    textArea.setCaretPosition(preFix.length() + pasteString.length());
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(e.getActionCommand().equals("Select All")) {
            textArea.setSelectionStart(0);
            textArea.setSelectionEnd(textArea.getText().length());
        }
        else if(e.getActionCommand().equals("Delete")) {
            if(selectedLength != 0) {
                try {
                    String preFix = textArea.getText(0, textArea.getSelectionStart());
                    String sufFix = textArea.getText(textArea.getSelectionEnd(), textArea.getText().length() - selectedLength - textArea.getSelectionStart());
                    textArea.setText(preFix + sufFix);
                    textArea.setCaretPosition(preFix.length());
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(e.getActionCommand().equals("Cut")) {
            if(selectedLength != 0) {
                StringSelection selectedString = new StringSelection(textArea.getSelectedText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selectedString , this);
                try {
                    String preFix = textArea.getText(0, textArea.getSelectionStart());
                    String sufFix = textArea.getText(textArea.getSelectionEnd(), textArea.getText().length() - selectedLength - textArea.getSelectionStart());
                    textArea.setText(preFix + sufFix);
                    textArea.setCaretPosition(preFix.length());
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(e.getActionCommand().equals("Undo")) {
            if(undoManager.canUndo()) {
                undoManager.undo();
                redoItem.setEnabled(true);
                popupRedoItem.setEnabled(true);
            }
        }
        else if(e.getActionCommand().equals("Redo")) {
            if(undoManager.canRedo()) {
                undoManager.redo();
            }
        }
        else if(e.getActionCommand().equals("About TextEditor")) {
            JDialog aboutDialog = new JDialog();
            JLabel aboutLabel = new JLabel("<html><body style='width:300;text-align:justify'><p>This is a try to make the introduction into a dialog box."
                    + "This dialog shall be big enough to consume a sufficient ammount of text to "
                    + "introduce the text editor program here. Clicking the close button will "
                    + "close the introduction dialog. This dialog shall be modal so the text editor "
                    + "can not be accessed during an open instance of the aboutDoialog."
                    + " It doesn't have many other features. But some features like an OK button,"
                    + "or a design or a background or an icon can be easily put in for making it"
                    + "more appropreate for better use.</html></body></p>");
            aboutDialog.setSize(350, 300);
            aboutDialog.setTitle(">>>Created By Keyur Golani<<<");
            aboutDialog.add(aboutLabel);
            aboutDialog.setLocationRelativeTo(this);
            aboutDialog.setModal(true);
            aboutDialog.setVisible(true);
        }
        else if(e.getActionCommand().equals("Title Case")) {
            if(selectedLength != 0) {
                try {
                    String preFix = textArea.getText(0, textArea.getSelectionStart());
                    String sufFix = textArea.getText(textArea.getSelectionEnd(), textArea.getText().length() - textArea.getSelectionEnd());
                    String convertedText = convertToTitleCase(textArea.getSelectedText());
                    textArea.setText(preFix + convertedText + sufFix);
                    textArea.setCaretPosition(preFix.length() + convertedText.length());
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(e.getActionCommand().equals("Lower Case")) {
            if(selectedLength != 0) {
                try {
                    String preFix = textArea.getText(0, textArea.getSelectionStart());
                    String sufFix = textArea.getText(textArea.getSelectionEnd(), textArea.getText().length() - textArea.getSelectionEnd());
                    String convertedText = textArea.getSelectedText().toLowerCase();
                    textArea.setText(preFix + convertedText + sufFix);
                    textArea.setCaretPosition(preFix.length() + convertedText.length());
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(e.getActionCommand().equals("Upper Case")) {
            if(selectedLength != 0) {
                try {
                    String preFix = textArea.getText(0, textArea.getSelectionStart());
                    String sufFix = textArea.getText(textArea.getSelectionEnd(), textArea.getText().length() - textArea.getSelectionEnd());
                    String convertedText = textArea.getSelectedText().toUpperCase();
                    textArea.setText(preFix + convertedText + sufFix);
                    textArea.setCaretPosition(preFix.length() + convertedText.length());
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(e.getActionCommand().equals("Text Wrap")) {
            if(wrapItem.isSelected()) {
                textArea.setLineWrap(true);
            }
            else {
                textArea.setLineWrap(false);
            }
        }
        else if(e.getActionCommand().equals("Bold Text")) {
            if(boldTextItem.isSelected()) {
                textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
            }
        }
        else if(e.getActionCommand().equals("Italic Text")) {
            if(italicTextItem.isSelected()) {
                textArea.setFont(textArea.getFont().deriveFont(Font.ITALIC));
            }
        }
        else if(e.getActionCommand().equals("Plain Text")) {
            if(plainTextItem.isSelected()) {
                textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
            }
        }
        else if(e.getActionCommand().equals("Date & Time")) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String dateString = dateFormat.format(date);
                String preFix = textArea.getText(0, textArea.getCaretPosition());
                String sufFix = textArea.getText(textArea.getCaretPosition(), textArea.getText().length() - textArea.getCaretPosition());
                textArea.setText(preFix + dateString + sufFix);
                textArea.setCaretPosition(preFix.length() + dateString.length());
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(e.getActionCommand().equals("Open")) {
            if(openFile == null && textArea.getText().equals("")) {
                int returnVal = fileChooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    openFile = fileChooser.getSelectedFile();
                    String openFileString = "";
                    try {
                        FileReader fr = new FileReader(openFile);
                        BufferedReader br = new BufferedReader(fr);
                        String ss;
                        while((ss = br.readLine()) != null) {
                            openFileString = openFileString + ss + "\n";
                        }
                        this.setTitle(openFile.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    textArea.setText(openFileString);
                }
            }
            else {
                int returnValue = JOptionPane.showConfirmDialog(this, "Do you want to save the current changes?", "Confirm Save", JOptionPane.YES_NO_CANCEL_OPTION);
                if(returnValue == JOptionPane.YES_OPTION) {
                    if(openFile == null) {
                        int anotherReturnValue = fileChooser.showSaveDialog(this);
                        if(anotherReturnValue == JFileChooser.APPROVE_OPTION) {
                            saveFile = fileChooser.getSelectedFile();
                        }
                    }
                    else {
                        saveFile = openFile;
                    }
                    FileWriter fw;
                    try {
                        fw = new FileWriter(saveFile);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(textArea.getText());
                        bw.flush();
                        bw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    int returnVal = fileChooser.showOpenDialog(this);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                        openFile = fileChooser.getSelectedFile();
                        String openFileString = "";
                        try {
                            FileReader fr = new FileReader(openFile);
                            BufferedReader br = new BufferedReader(fr);
                            String ss;
                            while((ss = br.readLine()) != null) {
                                openFileString = openFileString + ss + "\n";
                            }
                            this.setTitle(openFile.getName());
                        } catch (IOException ex) {
                            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        textArea.setText(openFileString);
                    }
                }
                else if(returnValue == JOptionPane.NO_OPTION) {
                    int returnVal = fileChooser.showOpenDialog(this);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                        openFile = fileChooser.getSelectedFile();
                        String openFileString = "";
                        try {
                            FileReader fr = new FileReader(openFile);
                            BufferedReader br = new BufferedReader(fr);
                            String ss;
                            while((ss = br.readLine()) != null) {
                                openFileString = openFileString + ss + "\n";
                            }
                            this.setTitle(openFile.getName());
                        } catch (IOException ex) {
                            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        textArea.setText(openFileString);
                    }
                }
            }
        }
        else if(e.getActionCommand().equals("Save")) {
            if(openFile == null) {
                int returnVal = fileChooser.showSaveDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile = fileChooser.getSelectedFile();
                    FileWriter fw;
                    try {
                        fw = new FileWriter(saveFile);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(textArea.getText());
                        bw.flush();
                        bw.close();
                        openFile = saveFile;
                        this.setTitle(saveFile.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else {
                saveFile = openFile;
                FileWriter fw;
                try {
                    fw = new FileWriter(saveFile);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(textArea.getText());
                    bw.flush();
                    bw.close();
                } catch (FileNotFoundException ex) {
                    int returnVal = fileChooser.showSaveDialog(this);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                        saveFile = fileChooser.getSelectedFile();
                        try {
                            fw = new FileWriter(saveFile);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(textArea.getText());
                        bw.flush();
                        bw.close();
                        openFile = saveFile;
                        this.setTitle(saveFile.getName());
                        } catch(IOException ek) {
                            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ek);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        else if(e.getActionCommand().equals("Save As")) {
            int returnVal = fileChooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                saveFile = fileChooser.getSelectedFile();
                FileWriter fw;
                try {
                    fw = new FileWriter(saveFile);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(textArea.getText());
                    bw.flush();
                    bw.close();
                    openFile = saveFile;
                    this.setTitle(saveFile.getName());
                } catch (IOException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(e.getActionCommand().equals("New")) {
            if(openFile == null && textArea.getText().equals("")) {
                //Do Nothing...
            }
            else {
                int returnValue = JOptionPane.showConfirmDialog(this, "Do you want to save the current changes?", "Confirm Save", JOptionPane.YES_NO_CANCEL_OPTION);
                if(returnValue == JOptionPane.YES_OPTION) {
                    if(openFile == null) {
                        int anotherReturnValue = fileChooser.showSaveDialog(this);
                        if(anotherReturnValue == JFileChooser.APPROVE_OPTION) {
                            saveFile = fileChooser.getSelectedFile();
                        }
                    }
                    else {
                        saveFile = openFile;
                    }
                    FileWriter fw;
                    try {
                        fw = new FileWriter(saveFile);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(textArea.getText());
                        bw.flush();
                        bw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    this.setTitle("Untitled");
                    textArea.setText("");
                    openFile = null;
                }
                else if(returnValue == JOptionPane.NO_OPTION) {
                    this.setTitle("Untitled");
                    textArea.setText("");
                    openFile = null;
                }
            }
        }
        else if(e.getActionCommand().equals("Text Color")) {
            Color foregroundColor = JColorChooser.showDialog(this, "Choose Text Color", textArea.getForeground());
            if(foregroundColor != null) {
                textArea.setForeground(foregroundColor);
            }
        }
        else if(e.getActionCommand().equals("Background Color")) {
            Color backgroundColor = JColorChooser.showDialog(this, "Choose Background Color", textArea.getBackground());
            if(backgroundColor != null) {
                textArea.setBackground(backgroundColor);
            }
        }
        else if(e.getActionCommand().equals("Font")) {
            String selectedFont = new FontChooser(this, textArea.getFont()).getSelectedFont();
            textArea.setFont(new Font(selectedFont, textArea.getFont().getStyle(), textArea.getFont().getSize()));
        }
        else if(e.getActionCommand().equals("Size")) {
            int selectedFontSize = new FontSizeChooser(this, textArea.getFont()).getSelectedSize();
            textArea.setFont(new Font(textArea.getFont().getName(), textArea.getFont().getStyle(), selectedFontSize));
        }
    }
    
    private String convertToTitleCase(String actualText) {
        StringBuilder text = new StringBuilder(actualText.toLowerCase());
        text.setCharAt(0, (char)(text.charAt(0) -32));
        for(int i = 1 ; i < text.length() ; i++){
            if(text.charAt(i -1) == ' ' && text.charAt(i) != ' '){
                text.setCharAt(i, (char)(text.charAt(i) -32));
            }
        }
        return text.toString();
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void componentResized(ComponentEvent e) {
        window = getBounds();
        textAreaScrollPane.setBounds(0, 30, window.width - 16, window.height - 30 - 39);
        textArea.setBounds(0, 30, window.width, window.height);
        menuBar.setBounds(0, 0, window.width, 30);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        //Do Nothing...
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //Do Nothing...
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //Do Nothing...
    }
    
}
