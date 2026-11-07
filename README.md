# Google Pixel 10 Launcher

A modern, highly customizable **Google Pixel 10 Launcher** built with **Kotlin** and **Jetpack Compose**, designed for all Android devices and fully optimized for **Honor X9d (MagicOS)** and Stock Android.

---

## ✨ Features

- 🎨 **Material You Dynamic Color Extraction:**
  Automatically extracts color palettes from your active home screen wallpaper (Obsidian Slate, Meadow Mint, Coral Rose, Bay Cyan) and applies them dynamically to the app drawer background, search bar, and themed icons.

- 🌤️ **At a Glance & Animated Weather:**
  Custom At a Glance widget with dynamic infinite Jetpack Compose animations for weather conditions (Rotating sun for sunny, falling raindrops for rain, floating clouds for cloudy).

- 🧠 **Smart Functional Folders & Auto-Grouping:**
  Drag one app icon onto another to automatically create smart folders suggested by category metadata (`Social & Chat`, `Games & Play`, `Productivity & Work`, `Utilities & Tools`). One-tap auto-grouping reorganizes your home screen cleanly.

- 🔍 **Dual Engine Search Dock:**
  Switch between **Google Search** and **Gemini Live AI** directly from the persistent Pixel dock.

- 📸 **Google Pixel Exclusive App Suite Integration:**
  Pre-configured shortcuts and metadata for Pixel Camera (*Night Sight*, *Take Selfie*), Pixel Weather, Pixel Studio (*Tensor G5 AI Canvas & Sticker Creator*), Pixel Recorder, Google Photos (*Magic Editor*), and Gemini Live.

- 📱 **Honor X9d & MagicOS Compatibility:**
  Configured with launcher intent filters (`android.intent.category.HOME`) and explicit package visibility queries for seamless default home launcher setup on Honor X9d / MagicOS.

- ⚡ **Pixel Gestures & Always-On Display (AOD):**
  Supports double-tap to sleep (AOD clock overlay), swipe-down for Quick Settings or App Drawer, and custom icon shapes (Squircle, Circle, Teardrop, Rounded Square).

- 💾 **Durable Room Database Persistence:**
  Saves layout positions, dock slots, custom app labels, folders, hidden apps, and settings reliably using Room DB.

---

## 🛠️ Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose + Material Design 3 (M3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Data Layer
- **Persistence:** Room Database (`LauncherDatabase`)
- **Async & Reactive Data:** Kotlin Coroutines + `StateFlow`
- **Testing:** Robolectric JVM Local Testing

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug / Koala or newer
- JDK 17
- Android SDK 35 (Min SDK 24)

### Building the APK
To build the APK locally using Gradle:
```bash
gradle assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📤 Exporting to GitHub & Generating APK in Google AI Studio

1. **Push to GitHub:**
   - Click on the **GitHub / Export** button in the top navigation bar of Google AI Studio.
   - Authorize your GitHub account and select or create the target repository.
2. **Download APK:**
   - Go to the **Settings / Build** menu in AI Studio and click **Export APK/AAB** to compile and download the installable APK file directly to your device.
