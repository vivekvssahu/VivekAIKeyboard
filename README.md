# Vivek AI Keyboard (Android MVP)

A native Kotlin Android keyboard prototype that uses the already-installed ChatGPT Android app through visible, user-authorized UI automation. It does **not** use an OpenAI API key or a separate AI backend.

## Included
- Android Input Method Service (custom keyboard)
- Smart Reply, Pickup Line, and Translate actions
- Reply tone presets: Natural, Flirty, Funny, Mature, Confident
- Reads selected text / text immediately before the cursor as conversation context
- Opens the installed ChatGPT app and asks the accessibility helper to enter the prompt
- Attempts to press ChatGPT's Send control and collect a visible response
- Stores a returned draft locally; when the user returns to the chat, they tap **Insert result**
- User remains responsible for reviewing and sending the message

## Important limitations
This is an experimental MVP, not a guaranteed fully background integration. The ChatGPT Android app does not expose a supported public local interface for third-party apps. Its UI and accessibility tree can change at any time. Some versions may not expose the composer, Send button, or response text to Accessibility; in those cases the automated step will not complete and the user must interact with ChatGPT manually. Launching ChatGPT takes focus away from the original chat, so the result is inserted after the user returns to the messaging app and reopens the keyboard. The keyboard cannot silently read other apps' full conversations; it uses only text exposed by Android's InputConnection (selected text / text before cursor).

Accessibility access is powerful. Enable it only if you trust this app, and disable it when not needed. Do not use it with passwords, financial apps, or sensitive content. No message is sent automatically to the recipient.

## Build APK
Requirements: Android Studio (or Android SDK + Gradle), JDK 17+, Android SDK Platform 35.

1. Open this folder in Android Studio.
2. Let Gradle sync and install the required Android SDK components.
3. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. The debug APK is normally created at `app/build/outputs/apk/debug/app-debug.apk`.

The current execution environment has Java but no Android SDK or Gradle installation, so an APK could not be compiled here. This folder contains the Android Studio project source.

## Install and setup
1. Install the official ChatGPT Android app and sign in.
2. Install the built Vivek AI Keyboard APK.
3. Open Vivek AI Keyboard and follow its setup buttons.
4. Enable **Vivek AI Keyboard** under Android keyboard settings and select it as the active keyboard.
5. Enable **Vivek AI Keyboard ChatGPT Helper** under Accessibility settings.
6. Open a messaging app, focus its message field, and use the keyboard actions.

## Package
`com.vivek.aikeyboard` — version 1.0 — min Android 8.0 (API 26), target API 35.
