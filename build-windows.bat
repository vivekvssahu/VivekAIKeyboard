@echo off
REM Run from an Android Studio / Gradle configured environment.
gradle assembleDebug
if errorlevel 1 exit /b 1
echo APK: app\build\outputs\apk\debug\app-debug.apk
