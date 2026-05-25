# Usage Guide

## What This Project Does

This IME is built for fast short-text voice input on Android:

- chat replies
- search queries
- short form fields
- short notes

The main path is:

1. open a text field
2. bring up the IME
3. tap `Start Voice`
4. speak
5. see realtime partial text in the IME
6. let the session auto-end on silence or stop manually
7. get final text inserted into the active field

## Installation

Build the APK:

```powershell
.\gradlew.bat :ime:assembleDebug
```

Install it:

```powershell
adb install -r .\android\ime\build\outputs\apk\debug\ime-debug.apk
```

Select the IME:

```powershell
adb shell ime set com.example.voiceinput.ime/.VoiceInputImeService
```

## Required Local Configuration

Repo root `local.properties`:

```properties
sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
dashscope.api.key=YOUR_DASHSCOPE_KEY
```

## UI Behavior

- `Start Voice`: starts a new voice session
- `Stop Voice`: manually ends the current session
- `Cancel Voice`: aborts without accepting more text
- realtime transcript area: shows partial recognition text
- `Undo Last Voice`: removes the last committed voice result

## Validation Checklist

Use this for manual validation:

1. Tap an input field and confirm the IME appears.
2. Tap `Start Voice`.
3. Confirm status changes to connecting/listening.
4. Speak a short Mandarin phrase.
5. Confirm realtime text appears in the IME.
6. Confirm final text is inserted into the real input field.
7. Confirm `Undo Last Voice` removes the last inserted result.
8. Confirm retry still works after a failed recognition attempt.

## Known MVP Limitations

- depends on network connectivity to DashScope
- tuned for Mandarin only
- not optimized yet for long dictation
- undo is currently limited to the last committed voice result
- no production-grade settings screen yet
