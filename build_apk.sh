#!/bin/bash

# Build script for VoidClient 2.0

echo "Building VoidClient 2.0 APK..."

# Check if gradlew exists and is executable
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found!"
    exit 1
fi

# Make gradlew executable if it isn't already
chmod +x ./gradlew

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "Building debug APK..."
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "Build failed!"
    exit 1
fi