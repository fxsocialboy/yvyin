# Voice Input MVP Docs

This folder documents the current Android IME MVP implementation.

## Documents

- [usage.md](./usage.md): install, enable, and validate the IME on a device
- [architecture.md](./architecture.md): current runtime structure and main data flow
- [privacy-audit.md](./privacy-audit.md): current privacy boundary and data handling notes

## Important Scope Note

These docs should reflect the current runnable MVP, not the original broader skeleton plan.

Current production recognition path:

- `AudioRecord` microphone capture
- DashScope realtime ASR
- realtime partial transcript in the IME
- final `commitText()` insertion into the active input field
