# Quick Start Guide

## Prerequisites
- Java 17 or later installed
- `JAVA_HOME` environment variable set (optional)

## Building

### Option 1: Using the build script (Recommended)
```bash
./build.sh
```

### Option 2: Using Maven (if available)
```bash
mvn clean package
```

### Option 3: Manual compilation
```bash
mkdir -p target/classes
javac -d target/classes --release 17 src/texteditordemo/*.java
jar cfe target/TextEditor.jar texteditordemo.TextEditorDemo -C target/classes .
```

## Running

### From JAR
```bash
java -jar target/TextEditor.jar
```

### From classes
```bash
java -cp target/classes texteditordemo.TextEditorDemo
```

## What's Changed?

This project has been modernized from Java 1.7 to Java 17. Key changes:

- ✅ Modern Java 17 language features
- ✅ Maven build system
- ✅ Automatic resource management (try-with-resources)
- ✅ Lambda expressions
- ✅ var keyword for type inference
- ✅ Modern date/time API
- ✅ No resource leaks

## Features

- **File**: New, Open, Save, Save As, Exit
- **Edit**: Undo, Redo, Cut, Copy, Paste, Delete, Select All, Date & Time
- **Format**: Font, Size, Text Color, Background Color, Case Tweaking, Text Styles, Text Wrap
- **Help**: About

## Keyboard Shortcuts

- **Z**: Undo
- **Y**: Redo
- **X**: Cut
- **C**: Copy
- **V**: Paste
- **A**: Select All
- **F**: Font
- **ESC**: Exit

## File Structure

```
TextEditor/
├── pom.xml                    # Maven configuration
├── build.sh                   # Build script
├── src/
│   └── texteditordemo/
│       ├── TextEditorDemo.java      # Main entry point
│       ├── TextEditor.java          # Editor window
│       ├── FontChooser.java         # Font dialog
│       └── FontSizeChooser.java     # Size dialog
├── target/
│   └── TextEditor.jar         # Executable JAR
├── README.md                  # Full documentation
├── MIGRATION_GUIDE.md         # Migration details
└── QUICKSTART.md              # This file
```

## Troubleshooting

### Build fails with "javac: command not found"
Install JDK 17 or later and ensure it's in your PATH.

### "UnsupportedClassVersionError" when running
You're using a JRE older than Java 17. Upgrade to Java 17+.

### "No X11 DISPLAY" error
This is normal in headless environments. The application requires a graphical display.

## Getting Help

- See `README.md` for detailed documentation
- See `MIGRATION_GUIDE.md` for modernization details
- Check the source code comments

## Quick Test

After building, verify it works:
```bash
# Check the JAR was created
ls -lh target/TextEditor.jar

# Try running (will fail in headless environments)
java -jar target/TextEditor.jar
```

## Next Steps

1. Read `README.md` for full documentation
2. Review `MIGRATION_GUIDE.md` to understand the changes
3. Explore the modernized source code
4. Build and run the application
5. Enjoy your modern Java text editor!
