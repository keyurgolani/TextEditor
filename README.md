# TextEditor - Modernized Java Text Editor

A fully-featured text editor application comprehensively modernized from Java 1.7 to Java 17 with modern build system, contemporary UI design, and current language features.

## What's New in Version 2.0

### Java Version
- **Upgraded from Java 1.7 to Java 17 LTS**
- Uses modern Java features including:
  - `var` keyword for local variable type inference
  - Lambda expressions for event handlers and callbacks
  - Try-with-resources for automatic resource management
  - Diamond operator (`<>`) for generic instantiation
  - Switch expressions for cleaner action handling
  - Modern date/time API (java.time package)
  - Enhanced type safety and null handling

### Build System
- **Migrated from Ant/NetBeans to Maven**
- Modern dependency management
- Standard Maven project structure
- Cross-platform compatibility
- Compiler warnings enabled for better code quality
- Maven exec plugin for easy execution

### UI Modernization
- **Modern Layout Managers**: Replaced absolute positioning (null layout) with BorderLayout and BoxLayout
- **System Look and Feel**: Uses native OS appearance for better integration
- **Responsive Design**: Window resizes properly without manual bounds calculation
- **Improved Dialogs**: Modern dialog layouts with proper spacing and alignment
- **Better Keyboard Shortcuts**: Full keyboard navigation with Ctrl+key combinations
- **Enhanced Font Chooser**: Preview fonts in their actual typeface
- **Improved Font Size Chooser**: Shows sizes with "pt" suffix for clarity
- **Modern About Dialog**: Clean, centered layout with proper typography

### Code Improvements
- **Proper Resource Management**: All file operations use try-with-resources to prevent resource leaks
- **Modern Event Handling**: Lambda expressions replace anonymous inner classes throughout
- **Type Safety**: Consistent use of var keyword where type is obvious, diamond operator for generics
- **Code Organization**: Better separation of concerns with extracted helper methods
- **Null Safety**: Better handling of null values and edge cases
- **No Static Abuse**: Removed inappropriate static fields in dialog classes
- **String Operations**: Use of `isEmpty()` instead of `equals("")`
- **Modern Character Handling**: Use `Character.toUpperCase()` instead of ASCII arithmetic

## Features

- **File Operations**: New, Open, Save, Save As with proper file handling
- **Edit Operations**: Undo, Redo, Cut, Copy, Paste, Delete, Select All
- **Text Formatting**: 
  - Font selection with live preview
  - Font size adjustment (8pt to 72pt)
  - Bold, Italic, Plain text styles
  - Text and background color customization
- **Text Manipulation**:
  - Title Case, Upper Case, Lower Case conversion
  - Text wrapping toggle
- **Utilities**:
  - Insert current date and time (F5)
  - Context menu (right-click)
  - Full keyboard shortcut support

## Keyboard Shortcuts

| Action | Shortcut |
|--------|----------|
| New | Ctrl+N |
| Open | Ctrl+O |
| Save | Ctrl+S |
| Save As | Ctrl+Shift+S |
| Undo | Ctrl+Z |
| Redo | Ctrl+Y |
| Cut | Ctrl+X |
| Copy | Ctrl+C |
| Paste | Ctrl+V |
| Delete | Delete |
| Select All | Ctrl+A |
| Date & Time | F5 |

## Building the Project

### Prerequisites
- Java 17 or later
- Maven 3.6 or later (for Maven builds)
- Or just Java 17+ javac for direct compilation

### Maven Build
```bash
mvn clean package
```

The executable JAR will be created at `target/TextEditor.jar`

### Direct Compilation (Without Maven)
```bash
# Compile
mkdir -p target/classes
javac -d target/classes --release 17 src/texteditordemo/*.java

# Create JAR
jar cfe target/TextEditor.jar texteditordemo.TextEditorDemo -C target/classes .
```

## Running the Application

### Using Maven
```bash
mvn exec:java
```

### Using the JAR
```bash
java -jar target/TextEditor.jar
```

### Direct Execution
```bash
java -cp target/classes texteditordemo.TextEditorDemo
```

## Project Structure

```
TextEditor/
├── pom.xml                          # Maven build configuration
├── src/
│   └── texteditordemo/
│       ├── TextEditorDemo.java      # Main application entry point
│       ├── TextEditor.java          # Main editor window
│       ├── FontChooser.java         # Font selection dialog
│       └── FontSizeChooser.java     # Font size selection dialog
└── target/                          # Build output directory
```

## Technical Details

### Modernization Changes

#### 1. Layout Management
**Before (Java 1.7):**
```java
setLayout(null);
component.setBounds(x, y, width, height);
addComponentListener(this);
// Manual resize handling in componentResized()
```

**After (Java 17):**
```java
setLayout(new BorderLayout());
add(component, BorderLayout.CENTER);
// Automatic resize handling by layout manager
```

#### 2. Try-with-Resources
**Before (Java 1.7):**
```java
FileWriter fw = new FileWriter(file);
BufferedWriter bw = new BufferedWriter(fw);
bw.write(text);
bw.flush();
bw.close(); // Manual resource management
```

**After (Java 17):**
```java
try (var writer = new BufferedWriter(new FileWriter(file))) {
    writer.write(text);
    writer.flush();
} // Automatic resource management
```

#### 3. Lambda Expressions
**Before:**
```java
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }
});
```

**After:**
```java
button.addActionListener(e -> doAction());
```

#### 4. Var Keyword
**Before:**
```java
DefaultListModel<String> model = new DefaultListModel<String>();
JList<String> list = new JList<>(model);
```

**After:**
```java
var model = new DefaultListModel<String>();
var list = new JList<>(model);
```

#### 5. Modern Date/Time API
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

#### 6. Switch Expressions
**Before:**
```java
if (command.equals("Save")) {
    handleSave();
} else if (command.equals("Open")) {
    handleOpen();
}
// ... many more else-if blocks
```

**After:**
```java
switch (command) {
    case "Save" -> handleSave();
    case "Open" -> handleOpen();
    default -> { /* Unknown command */ }
}
```

#### 7. Character Handling
**Before:**
```java
text.setCharAt(i, (char)(text.charAt(i) - 32)); // ASCII arithmetic
```

**After:**
```java
text.setCharAt(i, Character.toUpperCase(text.charAt(i))); // Modern API
```

#### 8. Instance Variables vs Static
**Before:**
```java
public class FontChooser {
    private static String selectedFont; // Shared across instances!
}
```

**After:**
```java
public class FontChooser {
    private String selectedFont; // Instance-specific
}
```

## Migration Notes

### From Ant to Maven
The legacy NetBeans Ant-based build system has been completely replaced with Maven:
- **Removed**: `build.xml`, `nbproject/` directory (kept for reference only)
- **Added**: `pom.xml` with standard Maven structure
- **Benefits**: Better dependency management, IDE-agnostic, industry standard, easier CI/CD integration

### Backward Compatibility
While the code now targets Java 17, all original functionality has been preserved:
- ✓ All menu items work identically
- ✓ All keyboard shortcuts preserved and enhanced
- ✓ File format compatibility maintained
- ✓ User interface improved while maintaining familiarity
- ✓ Better error handling and resource management

## Development

### Code Style
The modernized codebase follows these conventions:
- Use `var` for local variables when type is obvious from right-hand side
- Prefer lambda expressions for single-method interfaces (ActionListener, etc.)
- Always use try-with-resources for I/O operations
- Use diamond operator for generic instantiation
- Private access modifiers for fields, avoid inappropriate static usage
- Use modern layout managers (BorderLayout, BoxLayout, FlowLayout) instead of null layouts
- Extract complex logic into helper methods with clear names
- Use Character class methods instead of ASCII arithmetic

### Future Enhancements
Potential improvements for future versions:
- Add JUnit tests for core functionality
- Implement file encoding selection (UTF-8, ISO-8859-1, etc.)
- Add search and replace functionality
- Support for multiple documents (tabs)
- Syntax highlighting for code files
- Recent files menu
- Plugin system for extensions
- Dark mode support
- Auto-save functionality

## License

This is an educational project originally created by Keyur Golani.

## Author

Original Author: Keyur Golani  
Modernization: 2024

## Changelog

### Version 2.0.0 (2024)
- Upgraded to Java 17 LTS
- Migrated from Ant/NetBeans to Maven build system
- Comprehensively modernized code with Java 17 features
- Replaced null layouts with modern layout managers (BorderLayout, BoxLayout, FlowLayout)
- Implemented system Look and Feel for native OS appearance
- Added full keyboard shortcut support with modern accelerators
- Improved dialog designs with proper spacing and alignment
- Fixed resource leaks with try-with-resources
- Removed inappropriate static field usage
- Enhanced font chooser with font preview
- Improved font size chooser with "pt" suffix display
- Modernized About dialog with better typography
- Better error handling and null safety
- Used Character class methods instead of ASCII arithmetic
- Added default button support in dialogs
- Improved code organization and documentation

### Version 1.0.0 (Original)
- Java 1.7
- NetBeans/Ant build
- Basic text editor functionality
- Absolute positioning layouts

