/**
 * NotepadApp.java
 * A fully functional Notepad application built using Java Swing.
 * Supports creating, opening, saving, and editing plain text files.
 * Designed to demonstrate Swing components, event handling, and
 * file I/O in a clean, beginner-to-intermediate level project.
 *
 * Author  : Internship Submission
 * Project : Java Notepad Application
 *
 * HOW TO COMPILE AND RUN:
 *   javac NotepadApp.java
 *   java NotepadApp
 *
 * Features:
 *   - New / Open / Save / Save As / Exit
 *   - Undo / Redo support
 *   - Word Wrap toggle
 *   - Font size adjustment
 *   - Word and character count in status bar
 *   - Keyboard shortcuts (Ctrl+N, Ctrl+O, Ctrl+S, etc.)
 *   - Confirmation dialog before closing unsaved work
 *   - Dynamic title bar showing current file name
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NotepadApp extends JFrame {

    // -------------------------------------------------------
    // Core Components
    // -------------------------------------------------------
    private JTextArea   textArea;       // The main editing area
    private JScrollPane scrollPane;     // Scroll support for textArea
    private JLabel      statusBar;      // Bottom status bar (word/char count)

    // -------------------------------------------------------
    // Menu Components
    // -------------------------------------------------------
    private JMenuBar  menuBar;
    private JMenu     fileMenu, editMenu, viewMenu, helpMenu;

    // File menu items
    private JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem;

    // Edit menu items
    private JMenuItem undoItem, redoItem, cutItem, copyItem, pasteItem,
            selectAllItem, findItem;

    // View menu items
    private JCheckBoxMenuItem wordWrapItem;
    private JMenuItem increaseFontItem, decreaseFontItem;

    // Help menu items
    private JMenuItem aboutItem;

    // -------------------------------------------------------
    // State Variables
    // -------------------------------------------------------
    private File    currentFile;      // Currently opened file (null = untitled)
    private boolean isModified;       // Has the text been changed since last save?
    private int     currentFontSize;  // Tracks the current font size
    private UndoManager undoManager;  // Manages undo/redo history

    // -------------------------------------------------------
    // Application Constants
    // -------------------------------------------------------
    private static final String APP_NAME       = "Java Notepad";
    private static final int    DEFAULT_FONT    = 14;
    private static final int    MIN_FONT        = 8;
    private static final int    MAX_FONT        = 36;
    private static final String FONT_FACE       = "Monospaced";

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------

    /**
     * Sets up the entire Notepad application:
     * initializes components, builds the menu bar,
     * attaches listeners, and applies the layout.
     */
    public NotepadApp() {
        currentFontSize = DEFAULT_FONT;
        isModified      = false;
        undoManager     = new UndoManager();

        initializeFrame();
        buildTextArea();
        buildMenuBar();
        buildStatusBar();
        attachListeners();
        applyLayout();

        updateTitle();         // Set initial window title
        updateStatusBar();     // Show initial word/char count
    }

    // -------------------------------------------------------
    // Initialization Methods
    // -------------------------------------------------------

    /**
     * Configures the main JFrame properties.
     */
    private void initializeFrame() {
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // We handle close manually
        setLocationRelativeTo(null);   // Center window on screen

        // Use a slightly off-white background for a modern feel
        getContentPane().setBackground(new Color(248, 248, 248));
    }

    /**
     * Creates and configures the main JTextArea where the user types.
     * Wraps it in a JScrollPane so long files are scrollable.
     */
    private void buildTextArea() {
        textArea = new JTextArea();
        textArea.setFont(new Font(FONT_FACE, Font.PLAIN, currentFontSize));
        textArea.setLineWrap(true);          // Word wrap on by default
        textArea.setWrapStyleWord(true);     // Wrap at word boundaries
        textArea.setMargin(new Insets(8, 10, 8, 10)); // Comfortable padding
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(new Color(30, 30, 30));
        textArea.setCaretColor(new Color(30, 30, 30));
        textArea.setSelectionColor(new Color(173, 214, 255));

        // Register this text area with the undo manager
        textArea.getDocument().addUndoableEditListener(undoManager);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Builds the bottom status bar label.
     * Shows word count, character count, and current file path.
     */
    private void buildStatusBar() {
        statusBar = new JLabel("  Words: 0  |  Characters: 0");
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.setForeground(new Color(100, 100, 100));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setOpaque(true);
    }

    // -------------------------------------------------------
    // Menu Bar Construction
    // -------------------------------------------------------

    /**
     * Builds the full menu bar with File, Edit, View, and Help menus.
     * Assigns keyboard shortcuts (accelerators) to common actions.
     */
    private void buildMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 245, 245));
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        buildFileMenu();
        buildEditMenu();
        buildViewMenu();
        buildHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Creates the File menu with: New, Open, Save, Save As, and Exit items.
     */
    private void buildFileMenu() {
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F); // Alt+F to open menu

        newItem    = createMenuItem("New",     KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        openItem   = createMenuItem("Open...", KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
        saveItem   = createMenuItem("Save",    KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        saveAsItem = createMenuItem("Save As...", 0, 0);  // No shortcut
        exitItem   = createMenuItem("Exit",    KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
    }

    /**
     * Creates the Edit menu with: Undo, Redo, Cut, Copy, Paste,
     * Select All, and Find items.
     */
    private void buildEditMenu() {
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        undoItem      = createMenuItem("Undo",       KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        redoItem      = createMenuItem("Redo",       KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        cutItem       = createMenuItem("Cut",        KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
        copyItem      = createMenuItem("Copy",       KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
        pasteItem     = createMenuItem("Paste",      KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
        selectAllItem = createMenuItem("Select All", KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
        findItem      = createMenuItem("Find...",    KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);
        editMenu.addSeparator();
        editMenu.add(findItem);
    }

    /**
     * Creates the View menu with Word Wrap toggle and font size controls.
     */
    private void buildViewMenu() {
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        wordWrapItem = new JCheckBoxMenuItem("Word Wrap", true); // Checked by default

        increaseFontItem = createMenuItem("Increase Font Size", KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK);
        decreaseFontItem = createMenuItem("Decrease Font Size", KeyEvent.VK_MINUS,  InputEvent.CTRL_DOWN_MASK);

        viewMenu.add(wordWrapItem);
        viewMenu.addSeparator();
        viewMenu.add(increaseFontItem);
        viewMenu.add(decreaseFontItem);
    }

    /**
     * Creates the Help menu with an About dialog.
     */
    private void buildHelpMenu() {
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        aboutItem = createMenuItem("About Java Notepad", 0, 0);
        helpMenu.add(aboutItem);
    }

    /**
     * Helper method: creates a styled JMenuItem with an optional keyboard shortcut.
     *
     * @param text      The menu item label
     * @param keyCode   The key code (e.g., KeyEvent.VK_S). Use 0 for none.
     * @param modifiers The modifier mask (e.g., InputEvent.CTRL_DOWN_MASK). Use 0 for none.
     * @return A configured JMenuItem
     */
    private JMenuItem createMenuItem(String text, int keyCode, int modifiers) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("SansSerif", Font.PLAIN, 13));
        if (keyCode != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(keyCode, modifiers));
        }
        return item;
    }

    // -------------------------------------------------------
    // Layout Assembly
    // -------------------------------------------------------

    /**
     * Adds all components to the frame using BorderLayout.
     */
    private void applyLayout() {
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    // -------------------------------------------------------
    // Event Listeners
    // -------------------------------------------------------

    /**
     * Attaches all ActionListeners and DocumentListeners.
     * Keeps all event handling in one organized method.
     */
    private void attachListeners() {

        // --- File Menu Actions ---
        newItem.addActionListener(e -> handleNew());
        openItem.addActionListener(e -> handleOpen());
        saveItem.addActionListener(e -> handleSave());
        saveAsItem.addActionListener(e -> handleSaveAs());
        exitItem.addActionListener(e -> handleExit());

        // --- Edit Menu Actions ---
        undoItem.addActionListener(e -> {
            if (undoManager.canUndo()) undoManager.undo();
        });
        redoItem.addActionListener(e -> {
            if (undoManager.canRedo()) undoManager.redo();
        });
        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        selectAllItem.addActionListener(e -> textArea.selectAll());
        findItem.addActionListener(e -> handleFind());

        // --- View Menu Actions ---
        wordWrapItem.addActionListener(e -> toggleWordWrap());
        increaseFontItem.addActionListener(e -> adjustFontSize(2));
        decreaseFontItem.addActionListener(e -> adjustFontSize(-2));

        // --- Help Menu Action ---
        aboutItem.addActionListener(e -> showAboutDialog());

        // --- Window Close Button ---
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        // --- Document Listener: detect unsaved changes ---
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { markModified(); }
            @Override public void removeUpdate(DocumentEvent e)  { markModified(); }
            @Override public void changedUpdate(DocumentEvent e) { markModified(); }
        });
    }

    // -------------------------------------------------------
    // File Operation Handlers
    // -------------------------------------------------------

    /**
     * Creates a new blank document.
     * Prompts the user to save if there are unsaved changes.
     */
    private void handleNew() {
        if (isModified && !promptSaveBeforeAction()) return;

        textArea.setText("");
        currentFile = null;
        isModified  = false;
        undoManager.discardAllEdits();
        updateTitle();
        updateStatusBar();
    }

    /**
     * Opens an existing text file using a JFileChooser dialog.
     * Reads the file content and loads it into the text area.
     */
    private void handleOpen() {
        if (isModified && !promptSaveBeforeAction()) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open File");
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt")
        );

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                // Read the entire file content as a String
                String content = new String(Files.readAllBytes(Paths.get(currentFile.getAbsolutePath())));
                textArea.setText(content);
                textArea.setCaretPosition(0); // Scroll to top
                isModified = false;
                undoManager.discardAllEdits();
                updateTitle();
                updateStatusBar();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not open the file:\n" + ex.getMessage(),
                        "Open Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Saves the current document.
     * If no file is associated yet, falls back to Save As.
     */
    private void handleSave() {
        if (currentFile == null) {
            handleSaveAs(); // No file yet — use Save As
        } else {
            saveToFile(currentFile);
        }
    }

    /**
     * Opens a JFileChooser to choose a save location and filename.
     * Automatically appends ".txt" if no extension is provided.
     */
    private void handleSaveAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As");
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt")
        );

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Add .txt extension if the user didn't type one
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            // Warn before overwriting an existing file
            if (selectedFile.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(this,
                        "\"" + selectedFile.getName() + "\" already exists.\nDo you want to replace it?",
                        "Confirm Save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (overwrite != JOptionPane.YES_OPTION) return;
            }

            currentFile = selectedFile;
            saveToFile(currentFile);
        }
    }

    /**
     * Writes the current text area content to the specified file.
     *
     * @param file The file to write to
     */
    private void saveToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
            isModified = false;
            updateTitle();
            // Brief confirmation in status bar
            statusBar.setText("  File saved: " + file.getName()
                    + "  |  Words: " + countWords()
                    + "  |  Characters: " + textArea.getText().length());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save the file:\n" + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the application exit sequence.
     * Prompts the user to save if there are unsaved changes.
     */
    private void handleExit() {
        if (isModified) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes.\nDo you want to save before exiting?",
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                handleSave();
                // Only exit if save was successful (file is no longer modified)
                if (!isModified) System.exit(0);
            } else if (choice == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            // CANCEL — do nothing, return to the application
        } else {
            System.exit(0);
        }
    }

    // -------------------------------------------------------
    // Edit Operation Handlers
    // -------------------------------------------------------

    /**
     * Opens a simple Find dialog that highlights the first occurrence
     * of the search term in the text area.
     */
    private void handleFind() {
        String keyword = JOptionPane.showInputDialog(this,
                "Enter text to find:", "Find", JOptionPane.PLAIN_MESSAGE);

        if (keyword == null || keyword.isEmpty()) return;

        String text = textArea.getText();
        int index   = text.toLowerCase().indexOf(keyword.toLowerCase());

        if (index >= 0) {
            // Select and highlight the found text
            textArea.setSelectionStart(index);
            textArea.setSelectionEnd(index + keyword.length());
            textArea.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "\"" + keyword + "\" was not found in the document.",
                    "Find", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // -------------------------------------------------------
    // View Operation Handlers
    // -------------------------------------------------------

    /**
     * Toggles word wrap on or off based on the checkbox state.
     */
    private void toggleWordWrap() {
        boolean wrap = wordWrapItem.isSelected();
        textArea.setLineWrap(wrap);
        textArea.setWrapStyleWord(wrap);
    }

    /**
     * Increases or decreases the editor font size.
     * Clamped to MIN_FONT and MAX_FONT to prevent unusable extremes.
     *
     * @param delta The change in font size (positive = larger, negative = smaller)
     */
    private void adjustFontSize(int delta) {
        int newSize = currentFontSize + delta;

        if (newSize < MIN_FONT || newSize > MAX_FONT) {
            JOptionPane.showMessageDialog(this,
                    "Font size must be between " + MIN_FONT + " and " + MAX_FONT + ".",
                    "Font Size Limit", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        currentFontSize = newSize;
        textArea.setFont(new Font(FONT_FACE, Font.PLAIN, currentFontSize));
    }

    // -------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------

    /**
     * Marks the document as modified and refreshes the title bar.
     * Called every time the document content changes.
     */
    private void markModified() {
        if (!isModified) {
            isModified = true;
            updateTitle();
        }
        updateStatusBar();
    }

    /**
     * Updates the window title to reflect the current file name and
     * whether there are unsaved changes.
     * Format: "[*] filename — Java Notepad" or "Untitled — Java Notepad"
     */
    private void updateTitle() {
        String fileName = (currentFile != null) ? currentFile.getName() : "Untitled";
        String modified = isModified ? "* " : "";
        setTitle(modified + fileName + "  —  " + APP_NAME);
    }

    /**
     * Refreshes the status bar with the latest word and character counts.
     */
    private void updateStatusBar() {
        int wordCount = countWords();
        int charCount = textArea.getText().length();
        statusBar.setText("  Words: " + wordCount
                + "  |  Characters: " + charCount
                + (currentFile != null ? "  |  " + currentFile.getAbsolutePath() : ""));
    }

    /**
     * Counts the number of words in the text area.
     * Splits by whitespace and filters out empty tokens.
     *
     * @return Number of words
     */
    private int countWords() {
        String text = textArea.getText().trim();
        if (text.isEmpty()) return 0;
        return text.split("\\s+").length;
    }

    /**
     * Shows a dialog asking whether to save changes before a destructive action.
     *
     * @return true if the user saved or chose to discard, false if they cancelled
     */
    private boolean promptSaveBeforeAction() {
        int choice = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes.\nDo you want to save before continuing?",
                "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            handleSave();
            return !isModified; // Only continue if save succeeded
        } else if (choice == JOptionPane.NO_OPTION) {
            return true;  // Discard changes and continue
        }
        return false; // Cancel
    }

    /**
     * Shows an About dialog with application information.
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                APP_NAME + "\nVersion 1.0\n\n"
                        + "A simple text editor built with Java Swing.\n"
                        + "Features: New, Open, Save, Word Wrap,\n"
                        + "Undo/Redo, Find, and Font Size controls.\n\n"
                        + "Internship Submission Project",
                "About " + APP_NAME,
                JOptionPane.INFORMATION_MESSAGE);
    }

}