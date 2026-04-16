# VoidClient 2.0

An external Minecraft Bedrock Edition client for Android with Kotlin + Java + C++ (NDK) implementation.

## 📱 Features

- External memory manipulation without injection
- Real-time ESP (boxes, tracers, health display)
- Aim assist with configurable smoothing
- Overlay system for in-game rendering
- Modular architecture with clean separation of concerns
- Low-latency native performance

## 🏗️ Architecture

### Android Layer (Kotlin/Java)
- Jetpack Compose UI
- Background service for persistent operation
- Overlay rendering system
- JNI interface wrapper

### Native Layer (C++)
- Memory scanning and pattern recognition
- Process attachment via ptrace/process_vm_readv
- Entity data parsing
- Performance-optimized operations

### JNI Bridge
- Safe communication between Java/Kotlin and native code
- Memory read/write operations
- Entity data exchange

## 📁 Project Structure

```
VoidClient 2.0/
├── app/
│   ├── src/main/
│   │   ├── java/com/voidclient/
│   │   │   ├── ui/              # User interface components
│   │   │   ├── services/        # Background services
│   │   │   ├── native/          # JNI interface
│   │   │   ├── utils/           # Utility classes
│   │   │   └── config/          # Configuration management
│   │   ├── cpp/                 # Native C++ implementation
│   │   ├── jni/                 # JNI bridge files
│   │   └── res/                 # Resources
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## 🚀 Building

### Prerequisites
- Android Studio Flamingo or newer
- Android NDK (latest LTS)
- CMake 3.18.1 or newer

### Build Steps
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Build and run on device

```bash
# Clone the repository
git clone https://github.com/yourusername/voidclient-2.0.git

# Navigate to project directory
cd voidclient-2.0

# Build APK
./gradlew assembleDebug
```

## 🔧 Usage

1. Install the APK on your Android device
2. Grant overlay permission in settings
3. Launch VoidClient
4. Start the client service
5. Launch Minecraft Bedrock Edition
6. Enjoy enhanced gameplay features

## ⚠️ Disclaimer

This software is for educational purposes only. Use at your own risk. The developers are not responsible for any bans or penalties incurred by using this software.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.