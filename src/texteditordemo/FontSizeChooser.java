package texteditordemo;

import java.awt.*;
import javax.swing.*;

/**
 * Modern dialog for selecting font sizes.
 *
 * @author Keyur
 */
public class FontSizeChooser extends JDialog {
    
    private final JList<Integer> sizeList;
    private final Integer[] sizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
    private int selectedSize;
    private final int previousSize;

    public FontSizeChooser(JFrame parent, Font previousFont) {
        super(parent, "Choose Font Size", true);
        this.previousSize = previousFont.getSize();
        this.selectedSize = this.previousSize;
        
        // Use modern layout manager instead of null layout
        setLayout(new BorderLayout(10, 10));
        setSize(300, 500);
        setResizable(false);
        setLocationRelativeTo(parent);
        
        // Create size list with modern styling
        sizeList = new JList<>(sizes);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeList.setLayoutOrientation(JList.VERTICAL);
        sizeList.setVisibleRowCount(-1);
        sizeList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Set initial selection to current size
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] == previousSize) {
                sizeList.setSelectedIndex(i);
                sizeList.ensureIndexIsVisible(i);
                break;
            }
        }
        
        // Add custom cell renderer to show size preview
        sizeList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                var label = (JLabel) super.getListCellRendererComponent(
                    list, value + " pt", index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
        
        var sizeScrollPane = new JScrollPane(sizeList);
        sizeScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(sizeScrollPane, BorderLayout.CENTER);
        
        // Create button panel with modern layout
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        var okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> {
            var selected = sizeList.getSelectedValue();
            if (selected != null) {
                selectedSize = selected;
            }
            setVisible(false);
        });
        
        var cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> {
            selectedSize = previousSize;
            setVisible(false);
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set OK as default button
        getRootPane().setDefaultButton(okButton);
        
        setVisible(true);
    }
    
    public int getSelectedSize() {
        return selectedSize;
    }
}
