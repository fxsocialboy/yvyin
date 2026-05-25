# Voice Input MVP Architecture

## Current Runtime Modules

- `android/ime`
  - real `InputMethodService`
  - IME button and status UI
  - session wiring
  - `commitText()` insertion
- `android/core`
  - session models and state support used by the IME flow
- `backend/recognition-gateway`
  - currently not on the critical production path

## Current Production Recognition Path

1. User taps `Start Voice` inside the IME.
2. `VoiceInputImeService` starts a voice session.
3. `DashScopeRecognitionEngine` opens a realtime WebSocket session.
4. `AudioRecordCaptureEngine` captures 16 kHz mono PCM audio.
5. Audio chunks are streamed to DashScope.
6. Partial transcript events are shown inside the IME UI.
7. Speech end or manual stop closes the session.
8. Final transcript is returned.
9. `VoiceInputImeService` inserts final text through `currentInputConnection.commitText()`.

## Key Files

- `android/ime/src/main/kotlin/com/example/voiceinput/ime/VoiceInputImeService.kt`
- `android/ime/src/main/kotlin/com/example/voiceinput/transcription/cloud/DashScopeRecognitionEngine.kt`
- `android/ime/src/main/kotlin/com/example/voiceinput/transcription/cloud/DashScopeRealtimeAsrClient.kt`
- `android/ime/src/main/kotlin/com/example/voiceinput/transcription/local/AudioRecordCaptureEngine.kt`

## Deliberate Non-Goals In Current Code

- no production Huawei `SpeechRecognizer` dependency
- no heavy cloud orchestration layer
- no Room/DataStore driven personalization loop on the MVP path
- no benchmark-driven architecture expansion before core input flow stabilization
