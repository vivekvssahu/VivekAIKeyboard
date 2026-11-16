# Build Vivek AI Keyboard in GitHub Actions

1. Create a new GitHub repository named `VivekAIKeyboard`.
2. Upload the contents of this folder to the repository root (the files `settings.gradle.kts`, `build.gradle.kts`, `app/`, and `.github/` must be at the root).
3. Open the repository's **Actions** tab. If prompted, enable GitHub Actions.
4. Select **Build Android APK** and click **Run workflow**. A push to the `main` branch also triggers a build.
5. Wait for the workflow to finish successfully. Open the completed run, scroll to **Artifacts**, and download `VivekAIKeyboard-debug-apk`.
6. Extract the downloaded artifact ZIP. It contains `app-debug.apk`. Transfer it to your Android phone and install it. You may need to allow installation from that file manager/browser.

## Build locally in GitHub Codespaces (optional)

Open the repository in a Codespace, then run:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

# Install Android command-line tools / SDK in the Codespace first,
# or use the GitHub Actions workflow above (recommended).
gradle --version
gradle assembleDebug
```

The Actions workflow is recommended because it provisions JDK, Gradle, and Android SDK packages for you.

## Important prototype limitations

This is an experimental MVP. It does not call an OpenAI API. It attempts user-authorized interaction with the installed ChatGPT app via Android Accessibility. ChatGPT does not provide a supported public background interface for this, so automation may fail when its UI/accessibility tree changes or does not expose the composer/response. ChatGPT may open visibly. The user must review and insert the generated text; the app does not send messages to recipients automatically. Test on a real phone and avoid enabling accessibility for sensitive apps or content.
