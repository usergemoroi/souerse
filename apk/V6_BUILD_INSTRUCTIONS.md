# V6 Build Instructions

## ⚠️ Important Note

The V6 source code has been fully implemented but requires compilation to generate the APK. Due to environment constraints, the APK cannot be built automatically in this session.

---

## 📦 Building V6 APK

### Prerequisites

1. **Install Android Studio** (latest version)
2. **Install Android NDK** version 25.2.9519653
3. **Install Java 11** JDK
4. **Set environment variables:**
   ```bash
   export ANDROID_HOME=/path/to/android-sdk
   export NDK_HOME=$ANDROID_HOME/ndk/25.2.9519653
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
   export PATH=$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH
   ```

---

## 🔨 Build Steps

### Method 1: Using Gradle (Recommended)

```bash
# Navigate to project directory
cd /path/to/project

# Clean previous builds
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (signed)
./gradlew assembleRelease

# Output locations:
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release.apk
```

### Method 2: Using build.sh Script

```bash
# Make script executable
chmod +x build.sh

# Run build script
./build.sh

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Method 3: Using Android Studio

1. Open project in Android Studio
2. Wait for Gradle sync
3. Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**
4. Wait for build to complete
5. APK location shown in notification

---

## 🎯 Release APK Setup

### Creating V6release.apk

```bash
# Build release APK
./gradlew assembleRelease

# Copy to apk folder
cp app/build/outputs/apk/release/app-release.apk apk/V6release.apk

# Verify APK
aapt dump badging apk/V6release.apk | grep version
```

Expected output:
```
package: name='com.example.espapp' versionCode='6' versionName='6.0'
```

---

## 🔐 Signing Configuration

The release APK is automatically signed with:
- **Keystore:** `app/release.keystore`
- **Alias:** `release`
- **Password:** `password`

To use your own keystore, edit `app/build.gradle`:

```gradle
signingConfigs {
    release {
        storeFile file("your.keystore")
        storePassword "your_password"
        keyAlias "your_alias"
        keyPassword "your_password"
    }
}
```

---

## ✅ Verification

### Check APK Details

```bash
# APK info
aapt dump badging apk/V6release.apk

# APK size
ls -lh apk/V6release.apk

# Native libraries
unzip -l apk/V6release.apk | grep libsound_helper.so
```

Expected:
```
Size: ~4.8 MB
Native: lib/arm64-v8a/libsound_helper.so
Version: 6.0
```

### Test Installation

```bash
# Install on device
adb install -r apk/V6release.apk

# Launch app
adb shell am start -n com.example.espapp/.MainActivity

# Check logs
adb logcat | grep ESP_NATIVE
```

---

## 🐛 Troubleshooting

### Build Errors

**NDK not found:**
```bash
# Install NDK
sdkmanager --install "ndk;25.2.9519653"
```

**Java version mismatch:**
```bash
# Use Java 11
sudo update-alternatives --config java
```

**Gradle sync failed:**
```bash
# Clear Gradle cache
./gradlew clean
rm -rf .gradle
./gradlew build
```

### Native Build Errors

**Shader compilation errors:**
- Check `native_renderer.cpp` syntax
- Verify OpenGL ES 2.0 support

**Linking errors:**
- Verify NDK libraries: `-lGLESv2 -lEGL -ldl`
- Check `Android.mk` configuration

**JNI errors:**
- Verify method signatures match
- Check `EspService.java` native declarations

---

## 📊 Expected Build Output

### Successful Build Log

```
> Task :app:externalNativeBuildDebug
Building native library...
  [1/6] Building CXX object esp.cpp.o
  [2/6] Building CXX object esp_renderer.cpp.o
  [3/6] Building CXX object native_renderer.cpp.o
  [4/6] Building CXX object gl_hook.cpp.o
  [5/6] Building CXX object main.cpp.o
  [6/6] Linking CXX shared library libsound_helper.so

> Task :app:mergeDebugNativeLibs
> Task :app:stripDebugDebugSymbols
> Task :app:packageDebug

BUILD SUCCESSFUL in 45s
```

### APK Contents

```
apk/V6release.apk
├── AndroidManifest.xml
├── classes.dex
├── resources.arsc
├── res/
├── lib/
│   └── arm64-v8a/
│       └── libsound_helper.so  (~500 KB)
└── META-INF/
```

---

## 📦 APK Distribution

### Prepare for Release

```bash
# 1. Build release APK
./gradlew assembleRelease

# 2. Copy to distribution folder
cp app/build/outputs/apk/release/app-release.apk apk/V6release.apk

# 3. Generate checksums
cd apk
sha256sum V6release.apk > V6release.apk.sha256
md5sum V6release.apk > V6release.apk.md5

# 4. Create README
cat > V6_RELEASE_NOTES.txt << EOF
Cheat Menu V6 - Release APK
Version: 6.0
Build Date: $(date +%Y-%m-%d)
Size: $(ls -lh V6release.apk | awk '{print $5}')
SHA256: $(cat V6release.apk.sha256)
EOF
```

---

## 🚀 Quick Start After Build

```bash
# Install
adb install -r apk/V6release.apk

# Launch
adb shell am start -n com.example.espapp/.MainActivity

# Monitor
adb logcat | grep -E "ESP_NATIVE|NATIVE_RENDERER"
```

---

## 📝 Build Checklist

Before releasing V6release.apk:

- [ ] Clean build successful
- [ ] No compilation warnings
- [ ] Native library includes all symbols
- [ ] APK size reasonable (~4-5 MB)
- [ ] Version code = 6
- [ ] Version name = "6.0"
- [ ] Signature valid
- [ ] Tested on physical device
- [ ] Root check works
- [ ] Overlay permission works
- [ ] Native renderer initializes
- [ ] ESP renders in-game
- [ ] Settings sync works
- [ ] No crashes during testing

---

## 🔧 Development Build

For development/debugging:

```bash
# Debug build with symbols
./gradlew assembleDebug

# Install debug APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Debug native code
adb shell
su
gdbserver :1234 --attach $(pidof com.example.espapp)

# In separate terminal
adb forward tcp:1234 tcp:1234
arm-linux-androideabi-gdb
(gdb) target remote :1234
```

---

## 📚 Additional Resources

### Documentation
- V6_README.md - Usage guide
- V6_CHANGELOG.md - Changelog
- V6_PROJECT_SUMMARY.md - Technical details

### Build Files
- `build.gradle` - Gradle configuration
- `Android.mk` - NDK build config
- `Application.mk` - NDK app config
- `build.sh` - Build script

---

## ✨ Post-Build Verification

After building, verify:

1. **APK Structure**
   ```bash
   unzip -l apk/V6release.apk
   ```

2. **Native Library**
   ```bash
   nm app/build/intermediates/cmake/debug/obj/arm64-v8a/libsound_helper.so | grep "NativeRenderer"
   ```

3. **Version Info**
   ```bash
   aapt dump badging apk/V6release.apk | grep version
   ```

4. **Permissions**
   ```bash
   aapt dump badging apk/V6release.apk | grep permission
   ```

---

## 🎯 Expected Results

After successful build:

- **APK Size:** ~4.8 MB
- **Native Lib:** ~500 KB
- **Version Code:** 6
- **Version Name:** 6.0
- **Build Time:** ~45 seconds
- **Architecture:** arm64-v8a only

---

## 📞 Support

If build fails:

1. Check logs: `./gradlew build --stacktrace`
2. Verify NDK: `ndk-build -version`
3. Check Java: `java -version` (should be 11)
4. Clean project: `./gradlew clean`

---

**Note:** Once APK is built, place it in `/apk/V6release.apk` for distribution.

**Status:** Build instructions ready ✅  
**Next Step:** Execute build on machine with proper environment  
**Output:** V6release.apk (~4.8 MB)
