# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the connection between the measurement and its source file paths.
# This will be needed when creating an app bundle (with universalApk true) and doesn't affect regular apks.
#-keepattributes SourceFile, LineNumberTable

# Native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep our JNI interface
-keep class com.voidclient.native.** { *; }

# Keep our service classes
-keep class com.voidclient.services.** { *; }

# Keep our UI classes
-keep class com.voidclient.ui.** { *; }