@echo off
REM Build script for VoidClient 2.0

echo Building VoidClient 2.0 APK...

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    echo Error: gradlew.bat not found!
    exit /b 1
)

REM Clean previous builds
echo Cleaning previous builds...
call gradlew.bat clean

REM Build debug APK
echo Building debug APK...
call gradlew.bat assembleDebug

REM Check if build was successful
if %errorlevel% equ 0 (
    echo Build successful!
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo Build failed!
    exit /b 1
)