# Privacy Audit Notes

## Current Handling

- microphone audio is captured on-device with `AudioRecord`
- audio is sent to DashScope realtime ASR for production recognition
- API key is loaded locally from `local.properties`
- `local.properties` is gitignored and must not be committed

## Current Data Boundary

- on-device:
  - IME UI state
  - session control
  - audio capture
  - final text insertion
- cloud:
  - streamed audio for recognition
  - returned partial and final transcript text

## Current Constraints

- raw audio is not intentionally persisted by this repo as a product feature
- there is not yet a full end-user settings or disclosure screen
- this means privacy UX is not complete product-wise, even though the runtime boundary is known

## Release Caveat

Before any public distribution, user-facing disclosure and consent language should be completed and reviewed.
