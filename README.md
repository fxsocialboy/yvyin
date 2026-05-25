# YvYin

Android-first voice input method MVP focused on short text entry.

This project targets the real IME path instead of a demo-only shell:

- show a real `InputMethodService`
- capture microphone audio with `AudioRecord`
- send audio to Alibaba DashScope realtime ASR
- display realtime partial transcription
- auto-finish on silence when the ASR service reports speech end
- commit final text into the active input field with `currentInputConnection.commitText()`

## Current Status

Implemented and verified on real Android devices:

- IME can be installed, enabled, and selected
- voice button can start recording
- realtime partial text can be displayed in the IME
- final text can be committed into a real text field
- cancel / retry path exists
- basic undo of the last committed voice result exists

Still not in scope for this MVP:

- multilingual input
- heavy personalization
- local offline ASR
- benchmark suite completion
- long-form dictation productization

## Demo

- Demo video: [demo.mp4](./demo.mp4)

## Project Structure

```text
android/
  core/         shared models
  ime/          Android IME app and voice input flow
backend/
  recognition-gateway/  reserved for future gateway work
docs/
  voice-input-mvp/      architecture, usage, privacy notes
specs/
  001-voice-input-mvp/  original product spec and planning artifacts
```

## Build

Requirements:

- Android SDK 35
- Java 17
- an Android device or emulator
- DashScope API key with access to `qwen3-asr-flash-realtime`

Create `local.properties` in repo root:

```properties
sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
dashscope.api.key=YOUR_DASHSCOPE_KEY
```

Build:

```powershell
.\gradlew.bat :ime:assembleDebug
```

Install:

```powershell
adb install -r .\android\ime\build\outputs\apk\debug\ime-debug.apk
```

## Run

1. Enable `Voice Input IME` in Android input method settings.
2. Switch the current keyboard to `Voice Input IME`.
3. Open any app with a text field.
4. Tap `Start Voice`.
5. Speak Mandarin.
6. Watch realtime partial transcription inside the IME.
7. Wait for auto-finish or tap stop manually.
8. Confirm final text is inserted into the active field.

More detail:

- [Usage Guide](./docs/voice-input-mvp/usage.md)
- [Architecture](./docs/voice-input-mvp/architecture.md)
- [Privacy Notes](./docs/voice-input-mvp/privacy-audit.md)
- [Spec Kit Docs Index](./docs/voice-input-mvp/README.md)

## Technical Notes

- Production recognition path is Alibaba DashScope realtime ASR over WebSocket
- Huawei `SpeechRecognizer` is not the production path for this repo
- API key is loaded from `local.properties` and should not be committed

## License

No license file has been added yet.
