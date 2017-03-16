package texteditordemo;


import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.text.Utilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Keyur
 */
public class FontChooser extends JDialog implements ActionListener {
    
    JList fontList;
    static String selectedFont;
    static String previousFont;

    public FontChooser(JFrame parent, Font previousFont) {
        this.previousFont = previousFont.getName();
        setLayout(null);
        setSize(400, 400);
        setModal(true);
        setResizable(false);
        setTitle("Choose Font");
        setLocationRelativeTo(parent);
        
        DefaultListModel<String> fontListModel = new DefaultListModel<String>();
        fontList = new JList(fontListModel);
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = e.getAvailableFontFamilyNames();
        for(String s : fonts) {
            fontListModel.addElement(s);
        }
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.setLayoutOrientation(JList.VERTICAL);
        fontList.setVisibleRowCount(-1);
        JScrollPane fontScrollPane = new JScrollPane(fontList);
        fontScrollPane.setBounds(10, 10, 374, 301);
        add(fontScrollPane);
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setBounds(50, 326, 100, 30);
        add(okButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setBounds(224, 326, 100, 30);
        add(cancelButton);
        
        setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Cancel")) {
            selectedFont = previousFont;
            this.setVisible(false);
        }
        else {
            selectedFont = (String)fontList.getSelectedValue();
            this.setVisible(false);
        }
    }
    
    public static String getSelectedFont() {
        return selectedFont;
    }
    
    
    
    
}
