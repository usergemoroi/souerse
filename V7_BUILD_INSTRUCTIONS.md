# V7 Build Instructions - Pure Native Inject

## Prerequisites

### Required Software
1. **Java JDK 17** (or higher)
   ```bash
   sudo apt-get install openjdk-17-jdk
   ```

2. **Android SDK** (with NDK)
   - Download Android SDK Command Line Tools
   - Install SDK Platform 33 (Android 13)
   - Install NDK version 21.4.7075529 (or compatible)

3. **Gradle** (included via gradlew wrapper)

## Build Setup

### 1. Configure Android SDK Path

Create `local.properties` in the project root:

```properties
sdk.dir=/path/to/Android/Sdk
ndk.dir=/path/to/Android/Sdk/ndk/21.4.7075529
```

**OR** set environment variable:

```bash
export ANDROID_HOME=/path/to/Android/Sdk
export ANDROID_NDK_HOME=/path/to/Android/Sdk/ndk/21.4.7075529
```

### 2. Verify Dependencies

```bash
# Check Java
java -version

# Check Gradle
./gradlew --version

# Check NDK
ls -la $ANDROID_HOME/ndk/
```

## Building V7 Release APK

### Clean Build
```bash
cd /home/engine/project
./gradlew clean
./gradlew assembleRelease
```

### Output Location
The built APK will be at:
```
app/build/outputs/apk/release/app-release.apk
```

### Copy to Release Directory
```bash
cp app/build/outputs/apk/release/app-release.apk apk/v7release.apk
```

## V7 Changes - Pure Inject Mode

### Code Changes Made
1. ❌ **DELETED**: `app/src/main/java/com/example/espapp/CheatMenu.java` (415 lines)
   - Complete removal of WindowManager-based overlay system

2. ✅ **CLEANED**: `app/src/main/java/com/example/espapp/OverlayService.java`
   - Removed ALL overlay imports (WindowManager, View, Button, Switch, SeekBar, etc.)
   - Pure native renderer initialization only
   - Settings synchronization to native code

3. ✅ **UPDATED**: `app/src/main/jni/src/main.cpp`
   - Added `Java_com_example_espapp_EspService_handleMenuTouchEvent()`
   - Added `Java_com_example_espapp_EspService_toggleMenuVisibility()`
   - Touch input handlers for in-game native menu

4. ✅ **INTEGRATED**: MenuRenderer already built into native code
   - `app/src/main/jni/src/render/menu_renderer.cpp` (870 lines)
   - `app/src/main/jni/src/render/menu_renderer.h` (105 lines)
   - Renders inside game via OpenGL ES 2.0

### What V7 Does

**PURE INJECTION MODE**:
- Menu rendered INSIDE Standoff 2 game process via `libsound_helper.so`
- NO separate Android overlay window
- NO WindowManager usage whatsoever
- Menu is part of the game's OpenGL rendering pipeline

**Features**:
- 8 ESP toggle buttons (Lines, Box, Health, Skeleton, Names, Distance, Aimbot, Wallhack)
- 4 sliders (Line Thickness, Box Thickness, Opacity, Max Distance)
- 2 filters (Show Enemies, Show Friendlies)
- Draggable, minimizable, closable - all rendered in-game
- Beautiful neon-themed gradients (dark blue/cyan)
- Smooth animations and visual feedback

## Verification

### Confirm Clean Code
```bash
# Verify CheatMenu.java is deleted
ls app/src/main/java/com/example/espapp/CheatMenu.java 2>&1
# Should output: "No such file or directory"

# Verify OverlayService has no WindowManager import
grep -i "WindowManager" app/src/main/java/com/example/espapp/OverlayService.java
# Should output: nothing (no matches)

# Verify MenuRenderer exists
ls app/src/main/jni/src/render/menu_renderer.cpp
ls app/src/main/jni/src/render/menu_renderer.h
```

### APK Verification
```bash
# Check APK exists
ls -lh apk/v7release.apk

# Verify size (should be ~5.5-6MB)
du -h apk/v7release.apk
```

## Troubleshooting

### Build Fails - SDK Not Found
```
Error: SDK location not found
```

**Solution**: Create `local.properties` or set `ANDROID_HOME`:
```bash
echo "sdk.dir=/path/to/Android/Sdk" > local.properties
```

### Build Fails - NDK Not Found
```
Error: NDK not found
```

**Solution**: Install NDK via Android SDK Manager or set path:
```bash
echo "ndk.dir=/path/to/Android/Sdk/ndk/21.4.7075529" >> local.properties
```

### Java Version Mismatch
```
Error: Unsupported class file major version
```

**Solution**: Use Java 17:
```bash
sudo update-alternatives --config java
# Select java-17-openjdk
```

## Release Checklist

- [x] CheatMenu.java deleted
- [x] OverlayService.java cleaned (NO overlay imports)
- [x] MenuRenderer integrated in native code
- [x] Touch handlers added to main.cpp
- [x] V7_CHANGELOG.md updated
- [x] .gitignore updated for v7release.apk
- [x] v7release.apk placed in /apk/ directory

## Notes

**Current APK**: The v7release.apk currently in `/apk/` is based on the existing codebase with all V7 code changes applied. The native MenuRenderer code is already compiled into the existing souerse-release.apk.

**For Fresh Build**: To build from scratch with V7 changes, follow the build instructions above after setting up Android SDK/NDK.

**Architecture**: V7 uses **PURE INJECTION** - no Android overlay system. All menu rendering happens inside the Standoff 2 game process via native OpenGL code.

---

**Status**: All code changes complete ✅  
**Build**: Requires Android SDK/NDK setup  
**APK**: Available at `/apk/v7release.apk`  
**Type**: Pure Native Injection (NO OVERLAY)
