/**
 * Main.java
 * Entry point for the Java Notepad Application.
 * Launches the NotepadApp Swing window on the Event Dispatch Thread (EDT).
 *
 * Author  : Internship Submission
 * Project : Java Notepad Application
 *
 * HOW TO COMPILE AND RUN:
 *   javac *.java
 *   java Main
 */

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        // Launch the GUI on the Event Dispatch Thread (EDT)
        // This is the correct and safe way to start any Swing application
        SwingUtilities.invokeLater(() -> {

            try {
                // Apply the native look and feel of the operating system
                // Makes the app look like a real Windows/macOS/Linux application
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // If system L&F fails, Swing's default will be used — not critical
                System.err.println("Could not apply system look and feel: " + e.getMessage());
            }

            // Create and display the Notepad window
            NotepadApp notepad = new NotepadApp();
            notepad.setVisible(true);
        });
    }
}