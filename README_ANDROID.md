# MindFlip AAA - Android Studio Project Documentation

This folder contains the complete, production-ready **Android Studio** project wrapper for **MindFlip AAA Memory Challenge**.

## 📱 Tech Stack & Architecture
- **Language**: Kotlin 1.9+
- **Architecture**: MVVM (Model-View-ViewModel) + AndroidX + Material Design 3
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Native Bridges**:
  - `WebAppInterface.kt`: Enables JavaScript calls for Haptics/Vibration, AdMob Ads, and Google Play Billing.
  - `MainActivity.kt`: Hardware acceleration, Immersive Sticky Fullscreen, Offline Local Asset Loading, State Preservation across device rotations.
  - `MainViewModel.kt`: Preserves user state & billing observables across configuration changes.
- **Firebase Integrations**:
  - Firebase Analytics
  - Firebase Crashlytics
  - Firebase Cloud Messaging & Remote Config
- **Monetization & Play Services**:
  - Google Mobile Ads (AdMob) Interstitial & Rewarded Video Ads
  - Google Play Billing 6.2 (In-App Purchases)
  - Google Play App Review & In-App Updates

---

## 🚀 How to Build & Run in Android Studio

### Step 1: Export & Build Web Assets
First, compile the Web app into production static assets:
```bash
npm run build
```
This produces the optimized production output in the root `/dist` directory.

### Step 2: Copy Assets to Android Project
Copy the contents of the `/dist` folder into:
`/android/app/src/main/assets/www/`

*(Create the `assets/www/` directory inside `app/src/main` if it does not already exist).*

### Step 3: Open in Android Studio
1. Launch **Android Studio** (Hedgehog 2023.1.1 or higher).
2. Choose **Open an Existing Project** and select the `/android` folder.
3. Allow Gradle to sync dependencies.

### Step 4: Run on Emulator / Device
- Click **Run** (`Shift + F10`) to launch the app on an Android emulator or connected device.

---

## 🔒 ProGuard & Release Signing
To build a production signed APK or AAB for the Google Play Store:
1. In Android Studio, select **Build > Generate Signed Bundle / APK...**.
2. ProGuard and R8 rules are already pre-configured in `app/proguard-rules.pro` to keep JavaScript interfaces, Firebase serialization, and AdMob classes safely intact.
