package texteditordemo;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Dialog for selecting font families.
 *
 * @author Keyur
 */
public class FontChooser extends JDialog {
    
    private final JList<String> fontList;
    private static String selectedFont;
    private static String previousFont;

    public FontChooser(JFrame parent, Font previousFont) {
        FontChooser.previousFont = previousFont.getName();
        setLayout(null);
        setSize(400, 400);
        setModal(true);
        setResizable(false);
        setTitle("Choose Font");
        setLocationRelativeTo(parent);
        
        var fontListModel = new DefaultListModel<String>();
        fontList = new JList<>(fontListModel);
        var graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = graphicsEnvironment.getAvailableFontFamilyNames();
        for (String font : fonts) {
            fontListModel.addElement(font);
        }
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.setLayoutOrientation(JList.VERTICAL);
        fontList.setVisibleRowCount(-1);
        var fontScrollPane = new JScrollPane(fontList);
        fontScrollPane.setBounds(10, 10, 374, 301);
        add(fontScrollPane);
        
        var okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            selectedFont = fontList.getSelectedValue();
            setVisible(false);
        });
        okButton.setBounds(50, 326, 100, 30);
        add(okButton);
        
        var cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            selectedFont = FontChooser.previousFont;
            setVisible(false);
        });
        cancelButton.setBounds(224, 326, 100, 30);
        add(cancelButton);
        
        setVisible(true);
    }
    
    public static String getSelectedFont() {
        return selectedFont;
    }
}
