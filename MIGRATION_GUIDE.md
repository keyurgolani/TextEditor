# Migration Guide: Java 1.7 to Java 17

This document details all changes made during the modernization of the TextEditor application from Java 1.7 (NetBeans/Ant) to Java 17 (Maven).

## Build System Migration

### Old Build System (Ant/NetBeans)
```
TextEditor/
├── build.xml                    # Ant build file
├── nbproject/                   # NetBeans configuration
│   ├── build-impl.xml
│   ├── project.properties       # Java 1.7 configuration
│   ├── project.xml
│   └── genfiles.properties
└── manifest.mf
```

### New Build System (Maven)
```
TextEditor/
├── pom.xml                      # Maven configuration
└── build.sh                     # Alternative build script
```

**Key Configuration Changes:**
- Source/Target: Java 1.7 → Java 17
- Build Tool: Ant → Maven
- Project Structure: NetBeans layout → Standard Maven layout

## Code Modernization Changes

### 1. Package and Imports

#### TextEditorDemo.java
**Changes:**
- Added `SwingUtilities.invokeLater()` for thread safety
- Used lambda expression for Runnable

**Before:**
```java
public static void main(String[] args) {
    new TextEditor().setVisible(true);
}
```

**After:**
```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TextEditor().setVisible(true));
}
```

### 2. Field Declarations

#### All Classes
**Changes:**
- Added `private` access modifiers to fields
- Made immutable fields `final`
- Used diamond operator for generic instantiation

**Before:**
```java
JFileChooser fileChooser = new JFileChooser();
UndoManager undoManager = new UndoManager();
String title = "Untitled";
```

**After:**
```java
private final JFileChooser fileChooser = new JFileChooser();
private final UndoManager undoManager = new UndoManager();
private final String title = "Untitled";
```

### 3. Event Listeners

#### Lambda Expressions
**Changes:**
- Anonymous inner classes → Lambda expressions for simple listeners
- Cleaner, more readable code

**Before:**
```java
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        selectedFont = fontList.getSelectedValue();
        setVisible(false);
    }
});
```

**After:**
```java
button.addActionListener(e -> {
    selectedFont = fontList.getSelectedValue();
    setVisible(false);
});
```

#### CaretListener Example
**Before:**
```java
textArea.addCaretListener(new CaretListener() {
    @Override
    public void caretUpdate(CaretEvent e) {
        selectedLength = textArea.getSelectionEnd()-textArea.getSelectionStart();
        if(selectedLength != 0) {
            // enable menu items
        }
    }
});
```

**After:**
```java
textArea.addCaretListener(e -> {
    selectedLength = textArea.getSelectionEnd() - textArea.getSelectionStart();
    if (selectedLength != 0) {
        // enable menu items
    }
});
```

### 4. File I/O Operations

#### Try-with-Resources
**Major improvement:** Automatic resource management prevents resource leaks

**Before:**
```java
FileWriter fw;
try {
    fw = new FileWriter(saveFile);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(textArea.getText());
    bw.flush();
    bw.close();  // Manual closing
} catch (IOException ex) {
    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
}
```

**After:**
```java
try (var writer = new BufferedWriter(new FileWriter(saveFile))) {
    writer.write(textArea.getText());
    writer.flush();
} catch (IOException ex) {
    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
}
```

#### File Reading
**Before:**
```java
FileReader fr = new FileReader(openFile);
BufferedReader br = new BufferedReader(fr);
String ss;
while((ss = br.readLine()) != null) {
    openFileString = openFileString + ss + "\n";
}
// No explicit closing!
```

**After:**
```java
var content = new StringBuilder();
try (var reader = new BufferedReader(new FileReader(openFile))) {
    String line;
    while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
    }
} catch (IOException ex) {
    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
}
return content.toString();
```

### 5. Local Variable Type Inference (var)

**Changes:** Use `var` for local variables where type is obvious

**Examples:**
```java
// Before
DefaultListModel<String> fontListModel = new DefaultListModel<String>();
JScrollPane fontScrollPane = new JScrollPane(fontList);
ButtonGroup decorationButtons = new ButtonGroup();

// After
var fontListModel = new DefaultListModel<String>();
var fontScrollPane = new JScrollPane(fontList);
var decorationButtons = new ButtonGroup();
```

### 6. String Comparisons

**Changes:** Use `isEmpty()` instead of `equals("")`

**Before:**
```java
if(textArea.getText().equals("")) {
    saveItem.setEnabled(false);
}
```

**After:**
```java
if(textArea.getText().isEmpty()) {
    saveItem.setEnabled(false);
}
```

### 7. Date/Time Handling

**Changes:** Legacy Date → Modern java.time API

**Before:**
```java
DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
Date date = new Date();
String dateString = dateFormat.format(date);
```

**After:**
```java
var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
var dateString = LocalDateTime.now().format(formatter);
```

### 8. Action Handling

**Changes:** Long if-else chain → Switch expressions

**Before:**
```java
public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals("Copy")) {
        // handle copy
    }
    else if(e.getActionCommand().equals("Exit")) {
        // handle exit
    }
    // ... 20+ more else-if blocks
}
```

**After:**
```java
public void actionPerformed(ActionEvent e) {
    var command = e.getActionCommand();
    switch (command) {
        case "Copy" -> handleCopy();
        case "Exit" -> handleExit();
        // ... clean case statements
    }
}
```

### 9. Method Extraction

**Changes:** Extract repeated logic into helper methods

**New Helper Methods Added:**
- `handleWindowClosing()` - Consolidates window closing logic
- `saveFileContent(File)` - Encapsulates file saving
- `loadFileContent(File)` - Encapsulates file reading
- `openFileDialog()` - Manages file opening dialog
- `saveCurrentFile()` - Saves the current file
- `handleCopy()`, `handleCut()`, `handlePaste()`, etc. - Individual action handlers

### 10. Generic Type Parameters

**Changes:** Removed raw types, added proper generics

**Before:**
```java
JList fontList;  // Raw type
fontList = new JList(fontListModel);
```

**After:**
```java
private final JList<String> fontList;
fontList = new JList<>(fontListModel);  // Diamond operator
```

## Functional Changes

### Thread Safety
- Main window now created on Event Dispatch Thread using `SwingUtilities.invokeLater()`

### Resource Management
- All file operations now properly close resources
- No more resource leaks

### Error Handling
- Better exception handling with try-with-resources
- Consolidated error handling in helper methods

## Testing Verification

### Compilation
```bash
# Verify Java 17 compilation
javac -d target/classes --release 17 src/texteditordemo/*.java
```

### Functionality Testing
All existing functionality has been preserved:
- ✓ File operations (New, Open, Save, Save As)
- ✓ Edit operations (Undo, Redo, Cut, Copy, Paste, Delete)
- ✓ Text formatting (Font, Size, Colors, Styles)
- ✓ Text manipulation (Case conversions, Wrapping)
- ✓ Utilities (Date/Time insertion)
- ✓ Context menu
- ✓ Keyboard shortcuts

## Removed Files

The following legacy files are no longer needed:
- `build.xml` - Replaced by `pom.xml` and `build.sh`
- `nbproject/` directory - NetBeans-specific configuration
- `manifest.mf` - Now generated by Maven
- `build/` directory - Replaced by `target/`
- `dist/` directory - Replaced by `target/`

## New Files

- `pom.xml` - Maven project configuration
- `build.sh` - Alternative build script for non-Maven environments
- `README.md` - Comprehensive project documentation
- `MIGRATION_GUIDE.md` - This file
- `.gitignore` - Version control ignore patterns

## Breaking Changes

**None.** All public APIs and functionality remain identical. The application behaves exactly the same from a user perspective.

## Compatibility

- **Minimum Java Version:** Java 17 (was Java 1.7)
- **Build Tool:** Maven 3.6+ or direct javac compilation
- **Runtime:** Any Java 17+ JRE
- **File Format:** Unchanged, fully backward compatible

## Benefits of Migration

1. **Performance:** Java 17 includes numerous performance improvements
2. **Security:** Latest security patches and features
3. **Maintainability:** Modern, cleaner code is easier to maintain
4. **Resource Safety:** No resource leaks with try-with-resources
5. **Developer Experience:** Better IDE support, modern language features
6. **Long-term Support:** Java 17 is an LTS release (supported until 2029)
7. **Build System:** Maven provides better dependency management
8. **Cross-platform:** Works on any platform with Java 17+

## Known Issues

- None. All functionality works as expected.

## Future Improvements

Suggestions for future enhancements:
1. Add unit tests using JUnit 5
2. Implement file encoding detection and selection
3. Add search and replace functionality
4. Support multiple document tabs
5. Add syntax highlighting for code files
6. Implement auto-save functionality
7. Add recent files menu
8. Support for themes/skins

## References

- [Java 17 Release Notes](https://www.oracle.com/java/technologies/javase/17-relnote-issues.html)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Java Try-with-Resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
- [Lambda Expressions](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)
- [java.time API](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html)
