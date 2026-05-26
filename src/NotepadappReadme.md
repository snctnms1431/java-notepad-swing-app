# 📝 Java Notepad Application

A fully functional **Notepad / Text Editor** built with **Java Swing** as part of a Java Programming Internship project.
Supports creating, opening, saving, and editing plain text files with a clean, native-feeling GUI.

---

## 🖥️ Application Preview

The application opens as a standard desktop window with:
- A menu bar at the top (File | Edit | View | Help)
- A large text editing area in the center
- A status bar at the bottom showing word and character count

---

## ✨ Features

### File Operations
- ✅ **New** — Clear the editor and start fresh (Ctrl+N)
- ✅ **Open** — Browse and open any `.txt` file (Ctrl+O)
- ✅ **Save** — Save changes to the current file (Ctrl+S)
- ✅ **Save As** — Save to a new file name with `.txt` auto-append
- ✅ **Exit** — Close with unsaved-change protection (Ctrl+Q)

### Edit Operations
- ✅ **Undo / Redo** — Full undo history (Ctrl+Z / Ctrl+Y)
- ✅ **Cut / Copy / Paste** — Standard shortcuts (Ctrl+X / C / V)
- ✅ **Select All** — Select everything (Ctrl+A)
- ✅ **Find** — Case-insensitive text search with highlight (Ctrl+F)

### View Options
- ✅ **Word Wrap** — Toggle on/off via View menu
- ✅ **Increase / Decrease Font Size** — Ctrl+= and Ctrl+-
- ✅ **Scrollbar** — Auto-appears for long documents

### Professional Touches
- ✅ Dynamic title bar: `* filename.txt  —  Java Notepad`
- ✅ Unsaved changes prompt before New / Open / Exit
- ✅ Overwrite confirmation when saving to an existing file
- ✅ Live word count and character count in status bar
- ✅ Native OS look and feel (Windows/macOS/Linux)

---

## 🗂️ Project Structure

```
NotepadApp/
└── NotepadApp.java     → Single-file Swing application (all components in one class)
```

---

## ⚙️ Class & Method Overview

### `NotepadApp.java` extends `JFrame`

| Section | Methods | Description |
|---|---|---|
| Initialization | `initializeFrame()`, `buildTextArea()`, `buildStatusBar()` | Sets up the window and components |
| Menu Building | `buildMenuBar()`, `buildFileMenu()`, `buildEditMenu()`, `buildViewMenu()`, `buildHelpMenu()` | Constructs all menus |
| Event Handling | `attachListeners()` | Connects all menu actions to handlers |
| File Ops | `handleNew()`, `handleOpen()`, `handleSave()`, `handleSaveAs()`, `handleExit()` | Core file operations |
| Edit Ops | `handleFind()` | Text search with selection highlight |
| View Ops | `toggleWordWrap()`, `adjustFontSize(delta)` | View customization |
| Helpers | `markModified()`, `updateTitle()`, `updateStatusBar()`, `countWords()` | State management |

---

## 🚀 How to Compile and Run

### Prerequisites
- Java JDK 8 or higher (Swing is included in the standard JDK)
- A terminal, command prompt, or any Java IDE

### Steps

```bash
# 1. Navigate to the project folder
cd NotepadApp

# 2. Compile the Java file
javac NotepadApp.java

# 3. Run the application
java NotepadApp
```

### Running from an IDE (IntelliJ IDEA / Eclipse)
1. Create a new Java project
2. Copy `NotepadApp.java` into the `src/` folder
3. Right-click the file → Run

---

## 🛠️ Technologies & Swing Components Used

| Component | Purpose |
|---|---|
| `JFrame` | Main application window |
| `JTextArea` | Editable text editing area |
| `JScrollPane` | Scroll bars for the text area |
| `JMenuBar` | Top-level menu container |
| `JMenu` | Dropdown menu groups (File, Edit, etc.) |
| `JMenuItem` | Individual menu options |
| `JCheckBoxMenuItem` | Toggle option (Word Wrap) |
| `JFileChooser` | File open/save dialog |
| `JOptionPane` | Alert, confirm, and input dialogs |
| `ActionListener` | Handles button/menu click events |
| `DocumentListener` | Detects text changes for unsaved tracking |
| `UndoManager` | Provides undo/redo capability |
| `BufferedWriter` / `FileWriter` | Writes file content to disk |
| `Files.readAllBytes()` | Reads file content from disk |

---

## 📸 Suggested Screenshots for GitHub/LinkedIn

1. **Application on startup** — clean empty window with menu bar
2. **File > Open** in action — the file chooser dialog
3. **Typing text** — show the status bar updating with word count
4. **Find dialog** — with highlighted search result in text
5. **Unsaved changes prompt** — the save confirmation dialog
6. **Save As dialog** — file chooser for saving
7. **About dialog** — application info popup
8. **Word Wrap off** — horizontal scroll bar visible for long lines

---

## ⌨️ Keyboard Shortcuts Reference

| Shortcut | Action |
|---|---|
| Ctrl+N | New file |
| Ctrl+O | Open file |
| Ctrl+S | Save file |
| Ctrl+Q | Exit |
| Ctrl+Z | Undo |
| Ctrl+Y | Redo |
| Ctrl+X | Cut |
| Ctrl+C | Copy |
| Ctrl+V | Paste |
| Ctrl+A | Select all |
| Ctrl+F | Find |
| Ctrl+= | Increase font size |
| Ctrl+- | Decrease font size |

---

## 📄 License

This project is open-source and free to use for learning purposes.

---

> 💡 Built as part of a **Java Programming Internship** — demonstrates Java Swing, event-driven programming, file I/O, and GUI design.