# Voice Input MVP Architecture

## Modules

- `android/ime`: Android-first IME session, capture, UI, and continuation logic.
- `android/core`: shared models, privacy enforcement, telemetry aggregation.
- `android/data`: local settings, local persistence, network gateway client, personalization.
- `backend/recognition-gateway`: policy hooks and future cloud enhancement routing.
- `tests`: contract, integration, performance, and UX-scenario validation.

## Main Flow

1. User invokes voice input from the active field.
2. Local recognition starts immediately and produces partial text.
3. Cloud enhancement is gated by policy and network quality.
4. Text is inserted into the active field and remains editable inline.
5. Telemetry and privacy records are updated without default long-term raw-audio retention.

