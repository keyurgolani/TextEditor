package texteditordemo;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;

/**
 * Main text editor window with full editing capabilities.
 *
 * @author Keyur
 */
public class TextEditor extends JFrame implements ActionListener, ClipboardOwner, ComponentListener {
    
    private JPopupMenu rightClickMenu;
    private JMenuBar menuBar;
    private JTextArea textArea;
    private JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem;
    private JMenuItem undoItem, redoItem, cutItem, copyItem, pasteItem, deleteItem, selectAllItem, dateAndTimeItem;
    private JMenuItem fontItem, sizeItem, foregroundColorItem, backgroundColorItem;
    private JMenuItem popupUndoItem, popupRedoItem, popupCutItem, popupCopyItem, popupPasteItem, popupDeleteItem, popupSelectAllItem;
    private JMenuItem titleCaseItem, upperCaseItem, lowerCaseItem, aboutItem;
    private JCheckBoxMenuItem wrapItem;
    private JRadioButtonMenuItem boldTextItem, italicTextItem, plainTextItem;
    private File openFile, saveFile;
    private int selectedLength;
    private final JFileChooser fileChooser = new JFileChooser();
    private final UndoManager undoManager = new UndoManager();
    private Rectangle window;
    private final String title = "Untitled";
    private JScrollPane textAreaScrollPane;

    public TextEditor() throws HeadlessException {
        setTitle(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
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
        textArea.addCaretListener(e -> {
            selectedLength = textArea.getSelectionEnd() - textArea.getSelectionStart();
            if (selectedLength != 0) {
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
        });
        textArea.getDocument().addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
            undoItem.setEnabled(true);
            popupUndoItem.setEnabled(true);
        });
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!textArea.getText().isEmpty()) {
                    saveItem.setEnabled(true);
                    saveAsItem.setEnabled(true);
                    selectAllItem.setEnabled(true);
                    popupSelectAllItem.setEnabled(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (textArea.getText().isEmpty()) {
                    saveItem.setEnabled(false);
                    saveAsItem.setEnabled(false);
                    selectAllItem.setEnabled(false);
                    popupSelectAllItem.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not needed for plain text components
            }
        });
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
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
        textAreaScrollPane.setBounds(0, 30, window.width - 16, window.height - 30 - 39);
        add(textAreaScrollPane);
    }
    
    private void handleWindowClosing() {
        if (openFile == null && textArea.getText().isEmpty()) {
            System.exit(0);
        } else {
            int returnValue = JOptionPane.showConfirmDialog(this, 
                "Do you want to save the current changes?", 
                "Confirm Save", 
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (returnValue == JOptionPane.YES_OPTION) {
                if (openFile == null) {
                    int anotherReturnValue = fileChooser.showSaveDialog(this);
                    if (anotherReturnValue == JFileChooser.APPROVE_OPTION) {
                        saveFile = fileChooser.getSelectedFile();
                    }
                } else {
                    saveFile = openFile;
                }
                saveFileContent(saveFile);
                System.exit(0);
            } else if (returnValue == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        }
    }
    
    private void saveFileContent(File file) {
        try (var writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String loadFileContent(File file) {
        var content = new StringBuilder();
        try (var reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content.toString();
    }
    
    private JMenuBar makeMenu() {
        menuBar = new JMenuBar();
        rightClickMenu = new JPopupMenu();
        menuBar.setBounds(0, 0, window.width, 30);
        
        var fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        var editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
        var formatMenu = new JMenu("Format");
        menuBar.add(formatMenu);
        var helpMenu = new JMenu("Help");
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
        
        var caseTweakingSubMenu = new JMenu("Case Tweaking");
        var decorationSubMenu = new JMenu("Decoration");
        
        titleCaseItem = new JMenuItem("Title Case");
        upperCaseItem = new JMenuItem("Upper Case");
        lowerCaseItem = new JMenuItem("Lower Case");
        boldTextItem = new JRadioButtonMenuItem("Bold Text");
        italicTextItem = new JRadioButtonMenuItem("Italic Text");
        plainTextItem = new JRadioButtonMenuItem("Plain Text");
        
        var decorationButtons = new ButtonGroup();
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
        var command = e.getActionCommand();
        switch (command) {
            case "Copy" -> handleCopy();
            case "Exit" -> handleExit();
            case "Paste" -> handlePaste();
            case "Select All" -> handleSelectAll();
            case "Delete" -> handleDelete();
            case "Cut" -> handleCut();
            case "Undo" -> handleUndo();
            case "Redo" -> handleRedo();
            case "About TextEditor" -> handleAbout();
            case "Title Case" -> handleTitleCase();
            case "Lower Case" -> handleLowerCase();
            case "Upper Case" -> handleUpperCase();
            case "Text Wrap" -> handleTextWrap();
            case "Bold Text" -> handleBoldText();
            case "Italic Text" -> handleItalicText();
            case "Plain Text" -> handlePlainText();
            case "Date & Time" -> handleDateTime();
            case "Open" -> handleOpen();
            case "Save" -> handleSave();
            case "Save As" -> handleSaveAs();
            case "New" -> handleNew();
            case "Text Color" -> handleTextColor();
            case "Background Color" -> handleBackgroundColor();
            case "Font" -> handleFont();
            case "Size" -> handleSize();
        }
    }
    
    private void handleCopy() {
        if (selectedLength != 0) {
            var selectedString = new StringSelection(textArea.getSelectedText());
            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selectedString, this);
        }
    }
    
    private void handleExit() {
        handleWindowClosing();
    }
    
    private void handlePaste() {
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var contents = clipboard.getContents(null);
        boolean isTransferrable = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (isTransferrable) {
            try {
                var pasteString = (String) contents.getTransferData(DataFlavor.stringFlavor);
                var preFix = textArea.getText(0, textArea.getCaretPosition());
                var sufFix = textArea.getText(textArea.getCaretPosition(), 
                                              textArea.getText().length() - textArea.getCaretPosition());
                textArea.setText(preFix + pasteString + sufFix);
                textArea.setCaretPosition(preFix.length() + pasteString.length());
            } catch (UnsupportedFlavorException | IOException | BadLocationException ex) {
                Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void handleSelectAll() {
        textArea.setSelectionStart(0);
        textArea.setSelectionEnd(textArea.getText().length());
    }
    
    private void handleDelete() {
        if (selectedLength != 0) {
            try {
                var preFix = textArea.getText(0, textArea.getSelectionStart());
                var sufFix = textArea.getText(textArea.getSelectionEnd(), 
                                              textArea.getText().length() - selectedLength - textArea.getSelectionStart());
                textArea.setText(preFix + sufFix);
                textArea.setCaretPosition(preFix.length());
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void handleCut() {
        if (selectedLength != 0) {
            var selectedString = new StringSelection(textArea.getSelectedText());
            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selectedString, this);
            try {
                var preFix = textArea.getText(0, textArea.getSelectionStart());
                var sufFix = textArea.getText(textArea.getSelectionEnd(), 
                                              textArea.getText().length() - selectedLength - textArea.getSelectionStart());
                textArea.setText(preFix + sufFix);
                textArea.setCaretPosition(preFix.length());
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void handleUndo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
            redoItem.setEnabled(true);
            popupRedoItem.setEnabled(true);
        }
    }
    
    private void handleRedo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }
    
    private void handleAbout() {
        var aboutDialog = new JDialog();
        var aboutLabel = new JLabel("<html><body style='width:300;text-align:justify'><p>This is a modern Java text editor "
                + "with full editing capabilities including text formatting, undo/redo, and file operations. "
                + "It has been modernized to use Java 17 features and Maven build system. "
                + "Created by Keyur Golani and modernized for current Java standards.</p></body></html>");
        aboutDialog.setSize(350, 300);
        aboutDialog.setTitle(">>>Created By Keyur Golani<<<");
        aboutDialog.add(aboutLabel);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setModal(true);
        aboutDialog.setVisible(true);
    }
    
    private void handleTitleCase() {
        if (selectedLength != 0) {
            try {
                var preFix = textArea.getText(0, textArea.getSelectionStart());
                var sufFix = textArea.getText(textArea.getSelectionEnd(), 
                                              textArea.getText().length() - textArea.getSelectionEnd());
                var convertedText = convertToTitleCase(textArea.getSelectedText());
                textArea.setText(preFix + convertedText + sufFix);
                textArea.setCaretPosition(preFix.length() + convertedText.length());
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void handleLowerCase() {
        if (selectedLength != 0) {
            try {
                var preFix = textArea.getText(0, textArea.getSelectionStart());
                var sufFix = textArea.getText(textArea.getSelectionEnd(), 
                                              textArea.getText().length() - textArea.getSelectionEnd());
                var convertedText = textArea.getSelectedText().toLowerCase();
                textArea.setText(preFix + convertedText + sufFix);
                textArea.setCaretPosition(preFix.length() + convertedText.length());
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void handleUpperCase() {
        if (selectedLength != 0) {
            try {
                var preFix = textArea.getText(0, textArea.getSelectionStart());
                var sufFix = textArea.getText(textArea.getSelectionEnd(), 
                                              textArea.getText().length() - textArea.getSelectionEnd());
                var convertedText = textArea.getSelectedText().toUpperCase();
                textArea.setText(preFix + convertedText + sufFix);
                textArea.setCaretPosition(preFix.length() + convertedText.length());
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void handleTextWrap() {
        textArea.setLineWrap(wrapItem.isSelected());
    }
    
    private void handleBoldText() {
        if (boldTextItem.isSelected()) {
            textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
        }
    }
    
    private void handleItalicText() {
        if (italicTextItem.isSelected()) {
            textArea.setFont(textArea.getFont().deriveFont(Font.ITALIC));
        }
    }
    
    private void handlePlainText() {
        if (plainTextItem.isSelected()) {
            textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
        }
    }
    
    private void handleDateTime() {
        try {
            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            var dateString = LocalDateTime.now().format(formatter);
            var preFix = textArea.getText(0, textArea.getCaretPosition());
            var sufFix = textArea.getText(textArea.getCaretPosition(), 
                                          textArea.getText().length() - textArea.getCaretPosition());
            textArea.setText(preFix + dateString + sufFix);
            textArea.setCaretPosition(preFix.length() + dateString.length());
        } catch (BadLocationException ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleOpen() {
        if (openFile == null && textArea.getText().isEmpty()) {
            openFileDialog();
        } else {
            int returnValue = JOptionPane.showConfirmDialog(this, 
                "Do you want to save the current changes?", 
                "Confirm Save", 
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (returnValue == JOptionPane.YES_OPTION) {
                saveCurrentFile();
                openFileDialog();
            } else if (returnValue == JOptionPane.NO_OPTION) {
                openFileDialog();
            }
        }
    }
    
    private void openFileDialog() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            openFile = fileChooser.getSelectedFile();
            var content = loadFileContent(openFile);
            textArea.setText(content);
            setTitle(openFile.getName());
        }
    }
    
    private void saveCurrentFile() {
        if (openFile == null) {
            int anotherReturnValue = fileChooser.showSaveDialog(this);
            if (anotherReturnValue == JFileChooser.APPROVE_OPTION) {
                saveFile = fileChooser.getSelectedFile();
            }
        } else {
            saveFile = openFile;
        }
        saveFileContent(saveFile);
    }
    
    private void handleSave() {
        if (openFile == null) {
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                saveFile = fileChooser.getSelectedFile();
                saveFileContent(saveFile);
                openFile = saveFile;
                setTitle(saveFile.getName());
            }
        } else {
            saveFile = openFile;
            try {
                saveFileContent(saveFile);
            } catch (Exception ex) {
                int returnVal = fileChooser.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile = fileChooser.getSelectedFile();
                    saveFileContent(saveFile);
                    openFile = saveFile;
                    setTitle(saveFile.getName());
                }
            }
        }
    }
    
    private void handleSaveAs() {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            saveFile = fileChooser.getSelectedFile();
            saveFileContent(saveFile);
            openFile = saveFile;
            setTitle(saveFile.getName());
        }
    }
    
    private void handleNew() {
        if (openFile == null && textArea.getText().isEmpty()) {
            // Do nothing
        } else {
            int returnValue = JOptionPane.showConfirmDialog(this, 
                "Do you want to save the current changes?", 
                "Confirm Save", 
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (returnValue == JOptionPane.YES_OPTION) {
                saveCurrentFile();
                setTitle("Untitled");
                textArea.setText("");
                openFile = null;
            } else if (returnValue == JOptionPane.NO_OPTION) {
                setTitle("Untitled");
                textArea.setText("");
                openFile = null;
            }
        }
    }
    
    private void handleTextColor() {
        var foregroundColor = JColorChooser.showDialog(this, 
            "Choose Text Color", 
            textArea.getForeground());
        if (foregroundColor != null) {
            textArea.setForeground(foregroundColor);
        }
    }
    
    private void handleBackgroundColor() {
        var backgroundColor = JColorChooser.showDialog(this, 
            "Choose Background Color", 
            textArea.getBackground());
        if (backgroundColor != null) {
            textArea.setBackground(backgroundColor);
        }
    }
    
    private void handleFont() {
        String selectedFont = new FontChooser(this, textArea.getFont()).getSelectedFont();
        textArea.setFont(new Font(selectedFont, textArea.getFont().getStyle(), textArea.getFont().getSize()));
    }
    
    private void handleSize() {
        int selectedFontSize = new FontSizeChooser(this, textArea.getFont()).getSelectedSize();
        textArea.setFont(new Font(textArea.getFont().getName(), textArea.getFont().getStyle(), selectedFontSize));
    }
    
    private String convertToTitleCase(String actualText) {
        var text = new StringBuilder(actualText.toLowerCase());
        text.setCharAt(0, (char)(text.charAt(0) - 32));
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i - 1) == ' ' && text.charAt(i) != ' ') {
                text.setCharAt(i, (char)(text.charAt(i) - 32));
            }
        }
        return text.toString();
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // Not implemented - clipboard ownership is not critical for this application
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
        // Not needed
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // Not needed
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // Not needed
    }
}
