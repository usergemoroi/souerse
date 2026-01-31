# Cheat Menu V5 - Professional ESP System

## Overview
This is a fully-featured cheat menu (v5) with a professional interface and working ESP functions for Standoff 2.

## Features

### 1. Professional Cheat Menu (Overlay UI)
- **Dark theme with neon accents** - Stylish cyan, green, yellow, and magenta colors
- **Draggable interface** - Move the menu anywhere on the screen
- **Minimize/Maximize** - Collapse the menu to save screen space
- **Scrollable content** - Easy navigation through all features

### 2. ESP Functions (Fully Functional)
- **ESP Lines** - Lines from player to enemies
- **ESP Box** - Boxes around enemies with corner highlights
- **ESP Health Bars** - Visual health bar on the left side of box (color-coded by health)
- **ESP Skeleton** - Skeleton rendering (head, torso, arms, legs)
- **ESP Names** - Display player nicknames
- **ESP Distance** - Show distance to enemies in meters
- **Aimbot Indicator** - Crosshair indicator for aimbot
- **Wallhack Mode** - See enemies through walls

### 3. Settings System
- **Save/Load Settings** - All settings are automatically saved
- **Color Customization** - Each ESP feature has its own color
- **Thickness Adjustments** - Line thickness, box thickness sliders
- **Opacity Control** - Adjust transparency (10-100%)
- **Text Size** - Adjustable font size (8-24pt)
- **Max Distance** - Filter by distance (50-1000m)

### 4. Filters
- **Show Enemies** - Toggle enemy player visibility
- **Show Friendlies** - Toggle friendly player visibility
- **Reset to Defaults** - Quick reset button

### 5. Technical Implementation
- **Canvas Rendering** - Optimized drawing using Android Canvas API
- **60 FPS Target** - Smooth rendering with minimal performance impact
- **Auto-scaling** - Works on different screen resolutions
- **Memory Efficient** - Minimal resource usage

## File Structure

### Java Classes
- `CheatMenu.java` - Main cheat menu UI with toggles and sliders
- `EspRenderer.java` - ESP rendering engine (Canvas-based)
- `EspSettings.java` - Settings management and persistence
- `EspData.java` - Data structures for player information
- `OverlayService.java` - Service for overlay management
- `EspService.java` - Native ESP service handler
- `MainActivity.java` - Main application activity

### Resources
- `colors.xml` - Color definitions for the cheat menu
- Layout files for UI components

## Installation

1. Install the APK: `V5release.apk`
2. Grant overlay permission when prompted
3. Grant root access when prompted (required)
4. Click "Start" to inject the cheat
5. The cheat menu will appear as an overlay
6. Standoff 2 will launch automatically

## Usage

### Starting the Cheat
1. Open the app
2. Click "START" button
3. Grant necessary permissions
4. Cheat menu will appear and game will launch

### Using the Cheat Menu
1. Drag the title bar to move the menu
2. Click "_" to minimize
3. Click "X" to hide temporarily
4. Toggle features on/off with switches
5. Adjust settings with sliders
6. Changes save automatically

### Stopping the Cheat
1. Return to the app
2. Click "STOP" button
3. Or use the "X" button in the overlay

## Performance

- **Target FPS**: 60 FPS
- **Minimum FPS**: 30 FPS
- **Memory Usage**: ~50-100 MB
- **CPU Usage**: Low (optimized rendering)
- **Battery Impact**: Minimal

## Compatibility

- **Android Version**: 5.0+ (API 21+)
- **Architecture**: ARM64 only (arm64-v8a)
- **Root Required**: Yes
- **Game**: Standoff 2 (com.axlebolt.standoff2)

## Technical Details

### ESP Rendering
- Uses Android Canvas API for smooth drawing
- Anti-aliased lines and text
- Shadow effects for better visibility
- Color-coded health bars (green → yellow → red)
- Corner-highlighted boxes for premium look

### Settings Persistence
- SharedPreferences for data storage
- Automatic saving on changes
- Load on app start
- Reset to defaults option

### Overlay System
- TYPE_APPLICATION_OVERLAY for Android O+
- Non-focusable overlay (doesn't block game input)
- Translucent background
- Draggable interface

## Color Scheme

- **Primary Accent**: Cyan (#00FFFF)
- **Secondary Accent**: Green (#00FF00)
- **Warning**: Yellow (#FFFF00)
- **Danger**: Red (#FF0000)
- **Special**: Magenta (#FF00FF)
- **Background**: Dark Gray (#121212)
- **Text Primary**: White (#FFFFFF)
- **Text Secondary**: Light Gray (#CCCCCC)

## Security & Anti-Cheat

This is a proof-of-concept educational tool. Use at your own risk.
- Uses MOF (Memory Overlay Framework) for injection
- Native code obfuscation
- Root-level access for game memory reading
- TCP socket server on port 9557 for data streaming

## Credits

- **Version**: 5.0
- **Build**: Release
- **Date**: 2024

## Support

For issues or questions:
1. Check logs in the main app
2. Verify root access is granted
3. Ensure overlay permission is enabled
4. Check Standoff 2 is installed
5. Restart the device if needed

## Disclaimer

This is an educational project. Use responsibly and at your own risk.
Game cheating may violate terms of service and result in bans.
