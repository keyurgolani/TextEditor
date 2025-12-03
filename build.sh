#!/bin/bash

# Build script for TextEditor without Maven
# This script compiles the Java sources and creates an executable JAR

set -e

echo "=== TextEditor Build Script ==="
echo

# Check Java version
echo "Checking Java version..."
java -version
echo

# Clean previous builds
echo "Cleaning previous builds..."
rm -rf target
mkdir -p target/classes
echo "✓ Clean complete"
echo

# Compile Java sources
echo "Compiling Java sources with Java 17 target..."
javac -d target/classes \
      --release 17 \
      -Xlint:all \
      src/texteditordemo/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful"
else
    echo "✗ Compilation failed"
    exit 1
fi
echo

# Create JAR file
echo "Creating JAR file..."
jar cfe target/TextEditor.jar \
    texteditordemo.TextEditorDemo \
    -C target/classes .

if [ $? -eq 0 ]; then
    echo "✓ JAR created successfully"
else
    echo "✗ JAR creation failed"
    exit 1
fi
echo

# Display results
echo "=== Build Complete ==="
echo "Output: target/TextEditor.jar"
ls -lh target/TextEditor.jar
echo
echo "To run the application:"
echo "  java -jar target/TextEditor.jar"
