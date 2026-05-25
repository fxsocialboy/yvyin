# Implementation Plan: Voice Input MVP

**Branch**: `001-voice-input-mvp` | **Date**: 2026-05-25 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-voice-input-mvp/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See
`.specify/templates/plan-template.md` for the execution workflow.

## Summary

Deliver an Android-first voice input method focused on short-text entry for chat,
search, forms, and quick memos. The implementation uses a hybrid recognition path:
an on-device low-latency baseline for immediate capture and usable offline behavior,
plus cloud enhancement when network quality is acceptable. The primary product
differentiator is a lightweight main flow: invoke voice input, see text appear during
speech, correct quickly, and continue input without restarting the session.

## Technical Context

**Language/Version**: Kotlin 2.x, Android SDK 35, Java 17 toolchain
**Primary Dependencies**: Android InputMethodService APIs, Jetpack Compose, AndroidX,
DataStore, Room, OkHttp/WebSocket client, on-device speech engine adapter,
cloud-recognition gateway adapter
**Storage**: DataStore for settings, Room for local word-frequency adaptation and
session telemetry summaries, encrypted transient cache for in-flight audio buffers,
no raw-audio persistence by default
**Testing**: JUnit, AndroidX instrumentation tests, Compose UI tests, Macrobenchmark,
contract tests for cloud gateway, scenario-based manual validation
**Target Platform**: Android 12+ for MVP, iOS compatibility deferred to a later phase
**Project Type**: Mobile input-method product with Android IME client and
cloud-assisted speech gateway integration
**Performance Goals**: First visible transcription feedback within 300 ms median after
speech onset on supported devices, finalized short utterance within 800 ms median on
good networks, at least 25% faster task completion than keyboard baseline
**Constraints**: Must preserve a basic usable path under weak-network conditions, must
not retain raw audio long-term by default, must keep cloud usage selective to control
cost, must keep correction flow within the active text field
**Scale/Scope**: MVP aimed at pilot rollout for 10k Android DAU across chat, search,
forms, and short memo tasks, with Mandarin-only recognition and no multilingual mixed
input

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **User task defined - PASS**: The spec targets Android-first mobile users performing
  short-text entry in chat, search, forms, and brief memos where one-handed or mobile
  typing is inefficient.
- **Efficiency claim defined - PASS**: The feature is explicitly measured against
  keyboard-only and existing voice-input baselines using task completion time,
  correction count, and repeat-use intent.
- **Quality budgets defined - PASS**: The plan fixes concrete latency expectations,
  keeps recognition quality centered on punctuation, numbers, proper nouns, and
  correction burden, and constrains cloud usage for cost control.
- **Privacy boundary defined - PASS**: Listening control and baseline processing stay
  on-device where feasible; cloud enhancement is conditional, disclosed, and bounded
  by data minimization and no default long-term raw-audio retention.
- **Recovery path defined - PASS**: The design preserves accepted text, supports quick
  undo and manual editing, and guarantees fast keyboard fallback under recognition or
  network failure.
- **Validation plan defined - PASS**: Research and quickstart artifacts define
  scenario-based validation around real-user task completion, modification effort, and
  response-time behavior.

## Project Structure

### Documentation (this feature)

```text
specs/001-voice-input-mvp/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
└── contracts/
    ├── input-method-ui-contract.md
    └── cloud-recognition-interface.md
```

### Source Code (repository root)

```text
android/
├── ime/
│   ├── src/main/java/.../capture/
│   ├── src/main/java/.../session/
│   ├── src/main/java/.../transcription/
│   └── src/main/java/.../ui/
├── core/
│   ├── model/
│   ├── telemetry/
│   └── privacy/
├── data/
│   ├── local/
│   ├── network/
│   └── personalization/
└── benchmark/

backend/
└── recognition-gateway/
    ├── contracts/
    └── policy/

tests/
├── contract/
├── integration/
├── performance/
└── ux-scenarios/
```

**Structure Decision**: Use a mobile-plus-gateway structure. The Android IME owns
capture, session state, UI, local correction, and offline-capable baseline
recognition. The gateway exists only for cloud enhancement and policy enforcement, so
the mobile path remains usable even when the gateway is unavailable.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Hybrid recognition path | Needed to satisfy both low-latency baseline behavior and higher-quality enhancement under good network conditions | Pure cloud fails weak-network and latency constraints; pure local path risks unacceptable accuracy for proper nouns and punctuation |
| Cloud enhancement gateway | Needed to centralize disclosure, policy enforcement, cost control, and model routing for the enhancement path | Direct device-to-model integration reduces governance and cost control, and makes privacy-policy enforcement harder |

## Post-Design Constitution Re-check

- **Efficiency First - PASS**: The UI contract and quickstart keep the main path
  centered on invoke, transcribe, insert, correct, and continue for short-text tasks.
- **Accuracy Baseline - PASS**: Research, data model, and cloud interface all preserve
  explicit handling of punctuation, numbers, proper nouns, and correction burden.
- **Low Latency - PASS**: The hybrid design prioritizes immediate local feedback and
  allows cloud results to be ignored when they miss the latency budget.
- **Simplicity of Interaction - PASS**: Correction remains inline and lightweight, and
  no assistant-style command layer was introduced.
- **Cost Sustainability - PASS**: Cloud enhancement is policy-gated instead of being
  the default path for every utterance.
- **Privacy and Trust - PASS**: Contracts and data model enforce disclosure, bounded
  transfer modes, and no default long-term raw-audio retention.
- **Fallback and Recovery - PASS**: Both contracts and quickstart include weak-network,
  accidental-trigger, and manual-keyboard fallback behavior.
- **Real Scenario Validation - PASS**: Quickstart scenarios and success metrics remain
  tied to chat, search, form, and memo tasks rather than abstract benchmark-only goals.
