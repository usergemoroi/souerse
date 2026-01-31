# Cheat Menu V5 - Installation & Usage Guide

## 📱 Quick Start

### Prerequisites
- ✅ Android 5.0+ device (API 21+)
- ✅ ARM64 architecture (arm64-v8a)
- ✅ Rooted device with root access
- ✅ ~100 MB free storage
- ✅ Standoff 2 installed (optional but recommended)

## 🚀 Installation Steps

### Step 1: Transfer APK
1. Connect your Android device to computer via USB
2. Enable USB file transfer mode
3. Copy `/apk/V5release.apk` to device (Downloads folder recommended)
4. Disconnect device

**OR** use ADB:
```bash
adb push /path/to/V5release.apk /sdcard/Download/
```

### Step 2: Enable Unknown Sources
1. Open Android **Settings**
2. Navigate to **Security** or **Privacy**
3. Find **Install unknown apps** or **Unknown sources**
4. Enable for your file manager app (e.g., Files, ES File Explorer)

### Step 3: Install APK
1. Open **File Manager** on device
2. Navigate to **Downloads** folder
3. Tap on **V5release.apk**
4. Tap **Install**
5. Wait for installation to complete
6. Tap **Open** or find app in launcher

### Step 4: Grant Permissions

#### Overlay Permission (Required)
1. When prompted, tap **Allow**
2. OR go to Settings → Apps → ESP App → Display over other apps → Enable

#### Root Access (Required)
1. When prompted by SuperSU/Magisk, tap **Grant**
2. Enable "Remember choice" for automatic access
3. App will verify root access automatically

### Step 5: First Launch
1. Open **ESP App** from launcher
2. Wait for initialization (5-10 seconds)
3. Check status messages:
   - ✅ "Root access granted" (green)
   - ✅ "Overlay permission granted" (green)
4. Click **START** button

### Step 6: Cheat Menu Appears
1. Cheat menu overlay will appear on screen
2. If Standoff 2 is installed, it will launch automatically
3. Otherwise, launch Standoff 2 manually

## 🎮 Using the Cheat Menu

### Menu Navigation

#### Moving the Menu
- **Drag**: Touch and hold the title bar "⚡ CHEAT MENU V5 ⚡"
- **Move**: Drag to any position on screen
- **Release**: Drop at desired location

#### Minimizing/Maximizing
- **Minimize**: Tap the **"_"** button
- **Maximize**: Tap the **"□"** button (when minimized)
- **Effect**: Hides/shows menu content, keeps title bar visible

#### Hiding the Menu
- **Hide**: Tap the **"X"** button
- **Auto-show**: Menu reappears after 5 seconds
- **Manual show**: Return to main app and restart service

### ESP Features

#### Enabling/Disabling Features
Each feature has a toggle switch:
- **ON** (Blue): Feature is active
- **OFF** (Gray): Feature is disabled

Available toggles:
1. **ESP Lines** - Lines from screen bottom to enemy feet
2. **ESP Box** - Rectangles around enemies
3. **ESP Health Bars** - Health indicators on left of box
4. **ESP Skeleton** - Full body skeleton rendering
5. **ESP Names** - Player nicknames above boxes
6. **ESP Distance** - Distance in meters below boxes
7. **Aimbot Indicator** - Crosshair at enemy center
8. **Wallhack Mode** - See enemies through walls

#### Adjusting Settings
Use sliders to fine-tune values:

**Line Thickness** (1-10)
- Controls ESP line width
- Lower = thinner, Higher = thicker
- Default: 2.5

**Box Thickness** (1-10)
- Controls ESP box border width
- Lower = thinner, Higher = thicker
- Default: 2.5

**Text Size** (8-24)
- Controls name/distance font size
- Lower = smaller, Higher = larger
- Default: 14

**Opacity** (10-100)
- Controls overall ESP transparency
- Lower = more transparent, Higher = more opaque
- Default: 90%

**Max Distance** (50-1000)
- Filters players beyond this distance
- Lower = less visible, Higher = more visible
- Default: 500m

### Filters

**Show Enemies** (Toggle)
- ON: Display enemy players
- OFF: Hide enemy players

**Show Friendlies** (Toggle)
- ON: Display friendly players
- OFF: Hide friendly players

### Actions

**Reset to Defaults** (Button)
- Restores all settings to factory defaults
- Rebuilds menu with new settings
- All changes are saved automatically

## 🎨 Customization Tips

### For Maximum Visibility
- Set opacity to 100%
- Increase text size to 18-20
- Enable all ESP features
- Set max distance to 1000m

### For Minimal Intrusion
- Set opacity to 30-50%
- Decrease text size to 10-12
- Enable only essential features (Box, Names)
- Set max distance to 200-300m

### For Performance
- Disable skeleton rendering
- Reduce max distance to 300m
- Lower line/box thickness to 1.5-2.0
- Disable aimbot indicator if not needed

## 🔧 Troubleshooting

### Menu Not Appearing

**Check Permissions:**
1. Go to Settings → Apps → ESP App
2. Verify "Display over other apps" is enabled
3. Re-grant if needed

**Check Service:**
1. Open main ESP App
2. Look for "Status: Running" message
3. If stopped, click START button

**Check Visibility:**
1. Menu might be hidden (wait 5 seconds)
2. Menu might be off-screen (restart service)
3. Menu might be minimized (look for title bar)

### ESP Not Rendering

**Enable Features:**
1. Open cheat menu
2. Check that desired toggles are ON (blue)
3. Verify opacity is not too low (>50%)

**Check Game State:**
1. Ensure you're in an active match
2. Wait for players to spawn
3. Check that max distance includes nearby players

**Verify Root:**
1. Open main ESP App
2. Ensure root access is granted (green checkmark)
3. Re-grant root if needed

### Settings Not Saving

**Storage Permission:**
1. Go to Settings → Apps → ESP App
2. Verify storage permission is granted
3. Re-grant if needed

**Try Reset:**
1. Open cheat menu
2. Tap "Reset to Defaults"
3. Re-configure your settings
4. Test by restarting app

### Performance Issues

**Reduce Load:**
1. Disable unused ESP features
2. Lower max distance (300-400m)
3. Reduce line/box thickness
4. Lower text size

**Check Device:**
1. Close background apps
2. Free up RAM
3. Check CPU/GPU temperature
4. Update device firmware

### Game Crashes

**Compatibility:**
1. Ensure Standoff 2 is latest version
2. Check that device is ARM64
3. Verify root access is stable

**Memory:**
1. Free up device RAM
2. Clear game cache
3. Restart device

**Update:**
1. Game offsets may have changed
2. Check for cheat menu updates
3. Wait for compatible version

## 📊 Performance Tips

### Optimal Settings for 60 FPS
- Enable only needed features (Box, Names, Distance)
- Set max distance to 300-400m
- Use line thickness of 2.0
- Keep opacity at 80-90%
- Disable skeleton if not needed

### Battery Saving
- Minimize background apps
- Lower screen brightness
- Use power saving mode
- Reduce max distance
- Disable aimbot indicator

### Memory Management
- Restart app every 1-2 hours
- Clear game cache periodically
- Close unused apps
- Keep device cool

## 🎯 Advanced Usage

### Multiple Devices
- Install on each device separately
- Settings do not sync across devices
- Each installation is independent

### Updates
- Check for new versions regularly
- Backup settings before updating
- Clean install recommended for major updates

### Uninstallation
1. Stop ESP service in main app
2. Go to Settings → Apps → ESP App
3. Tap "Uninstall"
4. Confirm removal

## 🛡️ Safety & Security

### Anti-Cheat Detection
- Use at your own risk
- May result in game ban
- No guarantees of undetectability
- Educational purposes only

### Privacy
- No data sent to external servers
- Settings stored locally
- No personal information collected
- No analytics or tracking

### Permissions Explained
- **Overlay** - Required for menu display
- **Root** - Required for game memory access
- **Storage** - Required for settings persistence
- **Internet** - Future features (currently unused)
- **Network State** - Connection checking

## 💡 Tips & Tricks

### Best Practices
- ✅ Start ESP before launching game
- ✅ Keep menu in corner to avoid obstruction
- ✅ Use minimize when not adjusting settings
- ✅ Reset to defaults if experiencing issues
- ✅ Test in training mode first

### Common Mistakes to Avoid
- ❌ Don't set opacity too low (<30%)
- ❌ Don't enable all features at once (performance)
- ❌ Don't move menu during active gameplay
- ❌ Don't forget to grant root access
- ❌ Don't use on main account (ban risk)

### Pro Tips
- 💎 Use keyboard shortcuts if available
- 💎 Adjust opacity based on map brightness
- 💎 Lower max distance in close-quarters maps
- 💎 Increase distance in open maps
- 💎 Save multiple setting profiles (future feature)

## 📞 Support

### Getting Help
1. Check this guide thoroughly
2. Review README.md for feature details
3. Check CHANGELOG.md for recent changes
4. Review PROJECT_SUMMARY.md for technical info

### Reporting Issues
When reporting issues, include:
- Device model and Android version
- Root method (Magisk, SuperSU, etc.)
- Standoff 2 version
- Exact steps to reproduce
- Screenshots or logs if possible

### Community
- Educational purposes only
- Use responsibly
- Respect game terms of service
- Help others learn

## 🎓 Learning Resources

### Android Development
- Overlay services
- Canvas rendering
- SharedPreferences
- Broadcast receivers

### Game Hacking Concepts
- Memory reading/writing
- Process injection
- ESP techniques
- Anti-aliasing

### UI/UX Design
- Dark themes
- Neon color schemes
- Touch interfaces
- Material Design

## ✅ Quick Reference

### Essential Commands
```bash
# Install via ADB
adb install V5release.apk

# Push to device
adb push V5release.apk /sdcard/Download/

# Check if installed
adb shell pm list packages | grep espapp

# Uninstall
adb uninstall com.example.espapp
```

### Keyboard Shortcuts (Future)
- `Ctrl + M` - Minimize/Maximize
- `Ctrl + H` - Hide/Show
- `Ctrl + R` - Reset to Defaults
- `Ctrl + Q` - Quick toggle all

### Default Values
| Setting | Default | Range |
|---------|---------|-------|
| Line Thickness | 2.5 | 1-10 |
| Box Thickness | 2.5 | 1-10 |
| Text Size | 14 | 8-24 |
| Opacity | 90% | 10-100% |
| Max Distance | 500m | 50-1000m |

---

**Version**: 5.0  
**Last Updated**: 2024-01-31  
**Status**: Production Ready ✅

For more information, see:
- `CHEAT_MENU_V5_README.md` - Full documentation
- `V5_CHANGELOG.md` - Detailed changes
- `V5_PROJECT_SUMMARY.md` - Technical overview
