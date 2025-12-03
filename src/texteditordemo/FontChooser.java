package texteditordemo;

import java.awt.*;
import javax.swing.*;

/**
 * Modern dialog for selecting font families.
 *
 * @author Keyur
 */
public class FontChooser extends JDialog {
    
    private final JList<String> fontList;
    private String selectedFont;
    private final String previousFont;

    public FontChooser(JFrame parent, Font previousFont) {
        super(parent, "Choose Font", true);
        this.previousFont = previousFont.getName();
        this.selectedFont = this.previousFont;
        
        // Use modern layout manager instead of null layout
        setLayout(new BorderLayout(10, 10));
        setSize(450, 500);
        setResizable(false);
        setLocationRelativeTo(parent);
        
        // Create font list with modern styling
        var fontListModel = new DefaultListModel<String>();
        var graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var fonts = graphicsEnvironment.getAvailableFontFamilyNames();
        for (var font : fonts) {
            fontListModel.addElement(font);
        }
        
        fontList = new JList<>(fontListModel);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.setLayoutOrientation(JList.VERTICAL);
        fontList.setVisibleRowCount(-1);
        fontList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // Set initial selection to current font
        fontList.setSelectedValue(this.previousFont, true);
        
        // Add custom cell renderer to show fonts in their own style
        fontList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                var label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                try {
                    label.setFont(new Font(value.toString(), Font.PLAIN, 12));
                } catch (Exception e) {
                    // If font can't be loaded, use default
                }
                return label;
            }
        });
        
        var fontScrollPane = new JScrollPane(fontList);
        fontScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(fontScrollPane, BorderLayout.CENTER);
        
        // Create button panel with modern layout
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        var okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> {
            var selected = fontList.getSelectedValue();
            if (selected != null) {
                selectedFont = selected;
            }
            setVisible(false);
        });
        
        var cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> {
            selectedFont = this.previousFont;
            setVisible(false);
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set OK as default button
        getRootPane().setDefaultButton(okButton);
        
        setVisible(true);
    }
    
    public String getSelectedFont() {
        return selectedFont;
    }
}
