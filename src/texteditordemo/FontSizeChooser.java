package texteditordemo;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Dialog for selecting font sizes.
 *
 * @author Keyur
 */
public class FontSizeChooser extends JDialog {
    
    private final JList<Integer> sizeList;
    private final Integer[] sizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
    private static int selectedSize;
    private static int previousSize;

    public FontSizeChooser(JFrame parent, Font previousFont) {
        FontSizeChooser.previousSize = previousFont.getSize();
        setLayout(null);
        setSize(200, 400);
        setModal(true);
        setResizable(false);
        setTitle("Choose Font Size");
        setLocationRelativeTo(parent);
        
        sizeList = new JList<>(sizes);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeList.setLayoutOrientation(JList.VERTICAL);
        sizeList.setVisibleRowCount(-1);
        var sizeScrollPane = new JScrollPane(sizeList);
        sizeScrollPane.setBounds(0, 0, 194, 250);
        add(sizeScrollPane);
        
        var okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            selectedSize = sizeList.getSelectedValue();
            setVisible(false);
        });
        okButton.setBounds(47, 270, 100, 30);
        add(okButton);
        
        var cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            selectedSize = previousSize;
            setVisible(false);
        });
        cancelButton.setBounds(47, 320, 100, 30);
        add(cancelButton);
        
        setVisible(true);
    }
    
    public static int getSelectedSize() {
        return selectedSize;
    }
}
