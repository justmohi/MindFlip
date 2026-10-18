# MindFlip AAA — Android Project

This repository contains the Android Studio wrapper and the prebuilt Web app assets for MindFlip. The current repository does **not** include the original web-app source files or a `package.json`; therefore, `npm run build` cannot be run from this repository alone.

## Requirements

- Android Studio with support for Android Gradle Plugin 9.2
- JDK 17
- Android SDK Platform 34 (plus the SDK Build Tools required by the installed AGP)
- Internet access for the first Gradle dependency download

## Open and build

1. Clone or download this repository.
2. Open the **repository root** in Android Studio (the folder containing `settings.gradle.kts` and `gradlew.bat`). There is no separate `/android` subfolder.
3. Allow Gradle sync to finish and install any missing Android SDK components.
4. Build a debug APK:
   - Windows Command Prompt: `gradlew.bat assembleDebug`
   - Windows PowerShell: `./gradlew.bat assembleDebug`
   - macOS/Linux: `bash ./gradlew assembleDebug`
5. Find the APK at `app/build/outputs/apk/debug/app-debug.apk`.

For local validation, run these tasks from the repository root:

```bash
bash ./gradlew :app:assembleDebug :app:lintDebug
```

## Web assets

The prebuilt web app is stored in `app/src/main/assets/www/`. If you have the original web-app source project, build it there and copy the resulting static files into that directory. Keep the JavaScript and CSS filenames in `index.html` synchronized with the generated files.

## Firebase and ads

- `app/google-services.json` must belong to the Firebase Android app whose package name is `com.mindflip.memorygame`.
- The AdMob application ID and ad-unit IDs must be configured in Android resources for your own AdMob account before production release.
- Do not commit private service-account credentials or signing keystores.

## Google Play Billing status

Premium purchase and purchase restoration are intentionally **not granted locally**. The product ID, Play Console product configuration, and verified BillingClient purchase/acknowledgement flow must be implemented before enabling paid Premium features. A UI-only success callback is not a valid purchase.

## Release checklist

1. Configure and test the real Google Play Billing product and purchase restoration.
2. Verify Firebase and AdMob IDs for the production application.
3. Run `assembleRelease` and `lintRelease`, then test the signed build on physical devices.
4. Review privacy disclosures, data collection, ads, and Play Console requirements before publishing.
