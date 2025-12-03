# TextEditor - Modernized Java Text Editor

A fully-featured text editor application modernized from Java 1.7 to Java 17 with modern build system and language features.

## What's New in Version 2.0

### Java Version
- **Upgraded from Java 1.7 to Java 17 LTS**
- Uses modern Java features including:
  - `var` keyword for local variable type inference
  - Lambda expressions for event handlers
  - Try-with-resources for automatic resource management
  - Diamond operator (`<>`) for generic instantiation
  - Switch expressions for cleaner action handling
  - Modern date/time API (java.time package)

### Build System
- **Migrated from Ant/NetBeans to Maven**
- Modern dependency management
- Standard Maven project structure
- Cross-platform compatibility

### Code Improvements
- **Proper Resource Management**: All file operations now use try-with-resources to prevent resource leaks
- **Modern Event Handling**: Lambda expressions replace anonymous inner classes where appropriate
- **Type Safety**: Generic types properly specified throughout
- **Code Organization**: Better separation of concerns with extracted helper methods
- **Null Safety**: Better handling of null values and edge cases
- **String Operations**: Use of `isEmpty()` instead of `equals("")`

## Features

- **File Operations**: New, Open, Save, Save As
- **Edit Operations**: Undo, Redo, Cut, Copy, Paste, Delete, Select All
- **Text Formatting**: 
  - Font selection
  - Font size adjustment
  - Bold, Italic, Plain text styles
  - Text and background color customization
- **Text Manipulation**:
  - Title Case, Upper Case, Lower Case conversion
  - Text wrapping toggle
- **Utilities**:
  - Insert current date and time
  - Context menu (right-click)

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

#### 1. Try-with-Resources
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

#### 2. Lambda Expressions
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

#### 3. Var Keyword
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

#### 4. Modern Date/Time API
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

#### 5. Switch Expressions
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
    // ... clean case statements
}
```

## Migration Notes

### From Ant to Maven
The legacy NetBeans Ant-based build system has been replaced with Maven:
- Old: `build.xml`, `nbproject/` directory
- New: `pom.xml` with standard Maven structure
- Benefits: Better dependency management, IDE-agnostic, industry standard

### Backward Compatibility
While the code now targets Java 17, all original functionality has been preserved:
- ✓ All menu items work identically
- ✓ All keyboard shortcuts preserved
- ✓ File format compatibility maintained
- ✓ User interface unchanged

## Development

### Code Style
The modernized codebase follows these conventions:
- Use `var` for local variables when type is obvious
- Prefer lambda expressions for single-method interfaces
- Always use try-with-resources for I/O operations
- Use diamond operator for generic instantiation
- Private access modifiers for fields
- Extract complex logic into helper methods

### Future Enhancements
Potential improvements for future versions:
- Add JUnit tests
- Implement file encoding selection
- Add search and replace functionality
- Support for multiple documents (tabs)
- Syntax highlighting for code files
- Plugin system for extensions

## License

This is an educational project originally created by Keyur Golani.

## Author

Original Author: Keyur Golani  
Modernization: 2024

## Changelog

### Version 2.0.0 (2024)
- Upgraded to Java 17
- Migrated to Maven build system
- Modernized code with Java 17 features
- Fixed resource leaks
- Improved code organization
- Enhanced error handling

### Version 1.0.0 (Original)
- Java 1.7
- NetBeans/Ant build
- Basic text editor functionality
