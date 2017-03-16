/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texteditordemo;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Keyur
 */
public class FontSizeChooser extends JDialog implements ActionListener {
    
    JList sizeList;
    Integer[] sizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
    static int selectedSize;
    static int previousSize;

    public FontSizeChooser(JFrame parent, Font previousFont) {
        previousSize = previousFont.getSize();
        setLayout(null);
        setSize(200, 400);
        setModal(true);
        setResizable(false);
        setTitle("Choose Font");
        setLocationRelativeTo(parent);
        
        sizeList = new JList(sizes);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeList.setLayoutOrientation(JList.VERTICAL);
        sizeList.setVisibleRowCount(-1);
        JScrollPane sizeScrollPane = new JScrollPane(sizeList);
        sizeScrollPane.setBounds(0, 0, 194, 250);
        add(sizeScrollPane);
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setBounds(47, 270, 100, 30);
        add(okButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setBounds(47, 320, 100, 30);
        add(cancelButton);
        
        setVisible(true);
    }
    
    public static int getSelectedSize() {
        return selectedSize;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("OK")) {
            selectedSize = (int)sizeList.getSelectedValue();
            this.setVisible(false);
        }
        else {
            selectedSize = previousSize;
            this.setVisible(false);
        }
    }
    
    
}
