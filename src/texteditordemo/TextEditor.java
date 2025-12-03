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
 * Modernized for Java 17+ with contemporary UI design.
 *
 * @author Keyur
 */
public class TextEditor extends JFrame implements ActionListener, ClipboardOwner {
    
    private JPopupMenu rightClickMenu;
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
    private final String title = "Untitled";

    public TextEditor() throws HeadlessException {
        initializeLookAndFeel();
        setTitle(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        
        // Use BorderLayout for modern layout management
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());
        
        // Initialize text area with modern styling
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setMargin(new Insets(5, 5, 5, 5));
        
        // Add modern caret listener
        textArea.addCaretListener(e -> {
            selectedLength = textArea.getSelectionEnd() - textArea.getSelectionStart();
            var hasSelection = selectedLength != 0;
            cutItem.setEnabled(hasSelection);
            copyItem.setEnabled(hasSelection);
            deleteItem.setEnabled(hasSelection);
            titleCaseItem.setEnabled(hasSelection);
            upperCaseItem.setEnabled(hasSelection);
            lowerCaseItem.setEnabled(hasSelection);
            popupCutItem.setEnabled(hasSelection);
            popupCopyItem.setEnabled(hasSelection);
            popupDeleteItem.setEnabled(hasSelection);
        });
        
        // Add undo/redo support
        textArea.getDocument().addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
            undoItem.setEnabled(true);
            popupUndoItem.setEnabled(true);
        });
        
        // Document listener for save/select all menu items
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateMenuState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateMenuState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not needed for plain text components
            }
            
            private void updateMenuState() {
                var hasText = !textArea.getText().isEmpty();
                saveItem.setEnabled(hasText);
                saveAsItem.setEnabled(hasText);
                selectAllItem.setEnabled(hasText);
                popupSelectAllItem.setEnabled(hasText);
            }
        });
        
        // Add context menu support
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
        
        // Add text area with scroll pane using BorderLayout
        var scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Initialize modern look and feel for the application.
     */
    private void initializeLookAndFeel() {
        try {
            // Try to use system look and feel for native appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set modern UI defaults
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.WARNING, 
                "Could not set system look and feel", ex);
        }
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
    
    /**
     * Create the menu bar with modern keyboard shortcuts.
     */
    private JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();
        rightClickMenu = new JPopupMenu();
        
        // File Menu
        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        
        newItem = createMenuItem("New", KeyEvent.VK_N, 
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openItem = createMenuItem("Open", KeyEvent.VK_O, 
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem = createMenuItem("Save", KeyEvent.VK_S, 
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveAsItem = createMenuItem("Save As", KeyEvent.VK_A, 
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        exitItem = createMenuItem("Exit", KeyEvent.VK_X, null);
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        saveItem.setEnabled(false);
        saveAsItem.setEnabled(false);
        
        // Edit Menu
        var editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);
        
        undoItem = createMenuItem("Undo", KeyEvent.VK_U, 
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        redoItem = createMenuItem("Redo", KeyEvent.VK_R, 
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        cutItem = createMenuItem("Cut", KeyEvent.VK_T, 
            KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        copyItem = createMenuItem("Copy", KeyEvent.VK_C, 
            KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        pasteItem = createMenuItem("Paste", KeyEvent.VK_P, 
            KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        deleteItem = createMenuItem("Delete", KeyEvent.VK_D, 
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        selectAllItem = createMenuItem("Select All", KeyEvent.VK_A, 
            KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        dateAndTimeItem = createMenuItem("Date & Time", KeyEvent.VK_I, 
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        
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
        
        // Format Menu
        var formatMenu = new JMenu("Format");
        formatMenu.setMnemonic(KeyEvent.VK_O);
        menuBar.add(formatMenu);
        
        fontItem = createMenuItem("Font", KeyEvent.VK_F, null);
        sizeItem = createMenuItem("Size", KeyEvent.VK_S, null);
        foregroundColorItem = createMenuItem("Text Color", KeyEvent.VK_T, null);
        backgroundColorItem = createMenuItem("Background Color", KeyEvent.VK_B, null);
        wrapItem = new JCheckBoxMenuItem("Text Wrap");
        wrapItem.setMnemonic(KeyEvent.VK_W);
        wrapItem.setSelected(true);
        wrapItem.addActionListener(this);
        
        var caseTweakingSubMenu = new JMenu("Case Tweaking");
        var decorationSubMenu = new JMenu("Text Style");
        
        titleCaseItem = createMenuItem("Title Case", KeyEvent.VK_T, null);
        upperCaseItem = createMenuItem("Upper Case", KeyEvent.VK_U, null);
        lowerCaseItem = createMenuItem("Lower Case", KeyEvent.VK_L, null);
        boldTextItem = new JRadioButtonMenuItem("Bold");
        italicTextItem = new JRadioButtonMenuItem("Italic");
        plainTextItem = new JRadioButtonMenuItem("Plain");
        
        var decorationButtons = new ButtonGroup();
        decorationButtons.add(boldTextItem);
        decorationButtons.add(italicTextItem);
        decorationButtons.add(plainTextItem);
                
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
        
        // Context Menu (Right-click)
        popupUndoItem = createMenuItem("Undo", 0, null);
        popupRedoItem = createMenuItem("Redo", 0, null);
        popupCutItem = createMenuItem("Cut", 0, null);
        popupCopyItem = createMenuItem("Copy", 0, null);
        popupPasteItem = createMenuItem("Paste", 0, null);
        popupDeleteItem = createMenuItem("Delete", 0, null);
        popupSelectAllItem = createMenuItem("Select All", 0, null);
        
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
        
        // Help Menu
        var helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        aboutItem = createMenuItem("About TextEditor", KeyEvent.VK_A, null);
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Helper method to create menu items with modern keyboard shortcuts.
     */
    private JMenuItem createMenuItem(String text, int mnemonic, KeyStroke accelerator) {
        var item = new JMenuItem(text);
        if (mnemonic != 0) {
            item.setMnemonic(mnemonic);
        }
        if (accelerator != null) {
            item.setAccelerator(accelerator);
        }
        item.addActionListener(this);
        return item;
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
            case "Bold" -> handleBoldText();
            case "Italic" -> handleItalicText();
            case "Plain" -> handlePlainText();
            case "Date & Time" -> handleDateTime();
            case "Open" -> handleOpen();
            case "Save" -> handleSave();
            case "Save As" -> handleSaveAs();
            case "New" -> handleNew();
            case "Text Color" -> handleTextColor();
            case "Background Color" -> handleBackgroundColor();
            case "Font" -> handleFont();
            case "Size" -> handleSize();
            default -> { /* Unknown command */ }
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
        var aboutDialog = new JDialog(this, "About TextEditor", true);
        aboutDialog.setLayout(new BorderLayout(10, 10));
        aboutDialog.setSize(400, 250);
        aboutDialog.setResizable(false);
        
        // Create content panel with modern layout
        var contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        var titleLabel = new JLabel("TextEditor");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        var versionLabel = new JLabel("Version 2.0 - Modernized Edition");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        var descLabel = new JLabel("<html><div style='text-align: center; padding: 10px;'>"
                + "A modern Java text editor with full editing capabilities<br>"
                + "including text formatting, undo/redo, and file operations.<br><br>"
                + "Built with Java 17+ and Maven</div></html>");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        var authorLabel = new JLabel("Created by Keyur Golani");
        authorLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(versionLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(authorLabel);
        
        // Add close button with modern style
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        var closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(100, 30));
        closeButton.addActionListener(e -> aboutDialog.dispose());
        buttonPanel.add(closeButton);
        
        aboutDialog.add(contentPanel, BorderLayout.CENTER);
        aboutDialog.add(buttonPanel, BorderLayout.SOUTH);
        aboutDialog.setLocationRelativeTo(this);
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
        var fontChooser = new FontChooser(this, textArea.getFont());
        var selectedFont = fontChooser.getSelectedFont();
        if (selectedFont != null) {
            textArea.setFont(new Font(selectedFont, textArea.getFont().getStyle(), textArea.getFont().getSize()));
        }
    }
    
    private void handleSize() {
        var fontSizeChooser = new FontSizeChooser(this, textArea.getFont());
        var selectedFontSize = fontSizeChooser.getSelectedSize();
        if (selectedFontSize > 0) {
            textArea.setFont(new Font(textArea.getFont().getName(), textArea.getFont().getStyle(), selectedFontSize));
        }
    }
    
    private String convertToTitleCase(String actualText) {
        if (actualText == null || actualText.isEmpty()) {
            return actualText;
        }
        var text = new StringBuilder(actualText.toLowerCase());
        // Capitalize first character
        text.setCharAt(0, Character.toUpperCase(text.charAt(0)));
        // Capitalize first character after spaces
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i - 1) == ' ' && text.charAt(i) != ' ') {
                text.setCharAt(i, Character.toUpperCase(text.charAt(i)));
            }
        }
        return text.toString();
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // Not implemented - clipboard ownership is not critical for this application
    }
}
