# BECHIR MONEY Android APK
Android WebView wrapper for the existing GitHub Pages BECHIR MONEY app. Build with GitHub Actions workflow **Build BECHIR MONEY APK**; download artifact **BECHIR-MONEY-debug-APK** and extract app-debug.apk.

Important: This initial APK loads the hosted web app; it is not fully offline/self-contained. Firebase sign-in, cloud sync, file input, and external downloads must be tested in Android WebView. Debug APK is test-only, not a signed production release. App localStorage in WebView is separate from Chrome/PWA, so sign in and restore data carefully. Keep JSON backups.
