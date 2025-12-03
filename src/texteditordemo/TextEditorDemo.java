package texteditordemo;

import javax.swing.SwingUtilities;

/**
 * Main class for the TextEditor application.
 *
 * @author Keyur
 */
public class TextEditorDemo {

    /**
     * Application entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextEditor().setVisible(true));
    }
}
