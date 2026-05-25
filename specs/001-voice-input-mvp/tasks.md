---

description: "Task list for Voice Input MVP implementation"
---

# Tasks: Voice Input MVP

**Input**: Design documents from `/specs/001-voice-input-mvp/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Include contract, integration, performance, privacy, and recovery
validation tasks because the specification explicitly requires proof of accuracy,
latency, fallback behavior, privacy handling, and task-success improvements.

**Organization**: Tasks are grouped by user story to enable independent
implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Mobile app**: `android/ime/`, `android/core/`, `android/data/`, `android/benchmark/`
- **Gateway**: `backend/recognition-gateway/`
- **Tests**: `tests/contract/`, `tests/integration/`, `tests/performance/`, `tests/ux-scenarios/`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create the Android-first IME project skeleton and baseline tooling from the plan.

- [ ] T001 Create the planned Android, backend, and test directory structure in `android/ime/`, `android/core/`, `android/data/`, `android/benchmark/`, `backend/recognition-gateway/`, and `tests/`
- [ ] T002 Initialize Android build configuration and module settings in `android/settings.gradle.kts`, `android/build.gradle.kts`, `android/ime/build.gradle.kts`, `android/core/build.gradle.kts`, `android/data/build.gradle.kts`, and `android/benchmark/build.gradle.kts`
- [ ] T003 [P] Initialize the gateway package scaffold and dependency manifest in `backend/recognition-gateway/build.gradle.kts` or `backend/recognition-gateway/build.gradle`
- [ ] T004 [P] Configure Kotlin formatting, static analysis, and test tasks in `android/config/detekt.yml`, `android/.editorconfig`, and `android/gradle/libs.versions.toml`
- [ ] T005 [P] Create baseline project documentation stubs for implementation notes in `docs/voice-input-mvp/README.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Build shared session, privacy, telemetry, and recognition foundations that all user stories depend on.

**CRITICAL**: No user story work can begin until this phase is complete

- [ ] T006 Create core domain models for sessions, segments, field context, corrections, personalization, and privacy records in `android/core/model/src/main/kotlin/com/example/voiceinput/model/`
- [ ] T007 [P] Implement Room schema and DAO interfaces for session telemetry, personalization profiles, and privacy records in `android/data/local/src/main/kotlin/com/example/voiceinput/data/local/`
- [ ] T008 [P] Implement DataStore-backed settings and punctuation/session preferences in `android/data/local/src/main/kotlin/com/example/voiceinput/data/local/settings/VoiceInputSettingsStore.kt`
- [ ] T009 Implement the voice-input session state machine and transition reducer in `android/ime/src/main/kotlin/com/example/voiceinput/session/VoiceInputSessionManager.kt`
- [ ] T010 [P] Implement local recognition adapter interfaces and baseline transcription pipeline in `android/ime/src/main/kotlin/com/example/voiceinput/transcription/local/`
- [ ] T011 [P] Implement cloud enhancement client, policy gate, and timeout handling in `android/data/network/src/main/kotlin/com/example/voiceinput/data/network/cloud/`
- [ ] T012 Implement privacy policy enforcement and disclosure coordinator in `android/core/privacy/src/main/kotlin/com/example/voiceinput/privacy/PrivacyPolicyCoordinator.kt`
- [ ] T013 Implement telemetry hooks for task time, correction count, undo count, fallback rate, and first-feedback latency in `android/core/telemetry/src/main/kotlin/com/example/voiceinput/telemetry/VoiceInputMetricsRecorder.kt`
- [ ] T014 [P] Create shared fake recognizer, fake gateway, and session fixtures for tests in `tests/shared/src/main/kotlin/com/example/voiceinput/testdoubles/`
- [ ] T015 Implement IME service bootstrap, dependency wiring, and module composition root in `android/ime/src/main/kotlin/com/example/voiceinput/ime/VoiceInputImeService.kt`

**Checkpoint**: Foundation ready; user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Fast Short Text Entry (Priority: P1) MVP

**Goal**: Let Android users invoke voice input, see short-text transcription appear quickly, and keep typing or speaking without leaving the active field.

**Independent Test**: Validate chat reply and short search tasks end-to-end, including first visible transcription feedback, accepted text insertion, and continuation into a second utterance.

### Tests for User Story 1

- [ ] T016 [P] [US1] Add contract verification for UI session states and entry-point guarantees in `tests/contract/test_input_method_ui_contract.kt`
- [ ] T017 [P] [US1] Add cloud enhancement request/response contract tests in `tests/contract/test_cloud_recognition_interface.kt`
- [ ] T018 [P] [US1] Add integration test for chat reply transcription and insertion flow in `tests/integration/test_us1_chat_reply_flow.kt`
- [ ] T019 [P] [US1] Add integration test for search query transcription with natural pause handling in `tests/integration/test_us1_search_flow.kt`
- [ ] T020 [P] [US1] Add performance benchmark for first-feedback latency and utterance finalization in `tests/performance/test_us1_latency_benchmark.kt`

### Implementation for User Story 1

- [ ] T021 [P] [US1] Implement the voice-input trigger surface and listening-state UI in `android/ime/src/main/kotlin/com/example/voiceinput/ui/VoiceInputEntryView.kt`
- [ ] T022 [P] [US1] Implement partial and final transcript rendering into the active text field in `android/ime/src/main/kotlin/com/example/voiceinput/ui/ActiveFieldRenderer.kt`
- [ ] T023 [US1] Implement capture-session orchestration between IME service, session manager, and local recognizer in `android/ime/src/main/kotlin/com/example/voiceinput/capture/VoiceCaptureCoordinator.kt`
- [ ] T024 [US1] Implement hybrid transcription orchestration that promotes from local baseline to cloud enhancement when policy allows in `android/ime/src/main/kotlin/com/example/voiceinput/transcription/HybridTranscriptionCoordinator.kt`
- [ ] T025 [US1] Implement entry-context routing for chat, search, form, and memo modes in `android/ime/src/main/kotlin/com/example/voiceinput/session/TextFieldContextResolver.kt`
- [ ] T026 [US1] Implement session continuation behavior for sequential utterances in `android/ime/src/main/kotlin/com/example/voiceinput/session/VoiceInputContinuationController.kt`
- [ ] T027 [US1] Wire task-success telemetry and visible response-time instrumentation into the main flow in `android/ime/src/main/kotlin/com/example/voiceinput/telemetry/VoiceInputSessionTelemetryBinder.kt`

**Checkpoint**: User Story 1 should support the core invoke-transcribe-insert-continue loop and be testable independently

---

## Phase 4: User Story 2 - Quick Correction and Continuation (Priority: P2)

**Goal**: Let users repair recognition mistakes, adjust punctuation, undo recent insertions, and continue speaking without restarting the session.

**Independent Test**: Validate that a mistaken transcript can be edited inline, the last segment can be undone quickly, punctuation can be corrected or disabled, and resumed speech appends from the current cursor position.

### Tests for User Story 2

- [ ] T028 [P] [US2] Add integration test for inline correction and resumed voice continuation in `tests/integration/test_us2_inline_correction_flow.kt`
- [ ] T029 [P] [US2] Add integration test for undo-last-segment behavior in `tests/integration/test_us2_undo_last_segment.kt`
- [ ] T030 [P] [US2] Add UX scenario test for punctuation correction and temporary punctuation disable in `tests/ux-scenarios/test_us2_punctuation_controls.md`
- [ ] T031 [P] [US2] Add quality-budget validation for acceptable correction burden in `tests/performance/test_us2_correction_cost_metrics.kt`

### Implementation for User Story 2

- [ ] T032 [P] [US2] Implement inline correction tracking and correction action persistence in `android/ime/src/main/kotlin/com/example/voiceinput/session/CorrectionActionTracker.kt`
- [ ] T033 [P] [US2] Implement last-segment undo and retry controller in `android/ime/src/main/kotlin/com/example/voiceinput/session/SegmentUndoController.kt`
- [ ] T034 [US2] Implement punctuation-mode toggle and inline punctuation adjustment behavior in `android/ime/src/main/kotlin/com/example/voiceinput/ui/PunctuationControlPresenter.kt`
- [ ] T035 [US2] Implement cursor-aware append logic for resumed speech after manual edits in `android/ime/src/main/kotlin/com/example/voiceinput/session/CursorContinuationAppender.kt`
- [ ] T036 [US2] Implement local word-frequency adaptation updates from accepted text and corrections in `android/data/personalization/src/main/kotlin/com/example/voiceinput/data/personalization/WordFrequencyAdapter.kt`
- [ ] T037 [US2] Integrate correction metrics and acceptable-rate telemetry into the session recorder in `android/core/telemetry/src/main/kotlin/com/example/voiceinput/telemetry/CorrectionMetricsAggregator.kt`

**Checkpoint**: User Stories 1 and 2 should both work independently, with correction and continuation no longer requiring a reset

---

## Phase 5: User Story 3 - Reliable Recovery Under Imperfect Conditions (Priority: P3)

**Goal**: Keep the voice-input flow recoverable under weak network, cloud timeout, accidental trigger, cancellation, and focus-loss interruptions, with immediate keyboard fallback.

**Independent Test**: Validate weak-network fallback, accidental-trigger cancel, low-confidence failure handling, focus loss, and manual keyboard return without losing accepted text.

### Tests for User Story 3

- [ ] T038 [P] [US3] Add integration test for weak-network fallback from cloud enhancement to local-only mode in `tests/integration/test_us3_weak_network_fallback.kt`
- [ ] T039 [P] [US3] Add integration test for accidental-trigger cancel with no unintended insertion in `tests/integration/test_us3_accidental_trigger_cancel.kt`
- [ ] T040 [P] [US3] Add integration test for low-confidence or no-speech failure recovery in `tests/integration/test_us3_recognition_failure_recovery.kt`
- [ ] T041 [P] [US3] Add UX scenario test for app-switch interruption and keyboard fallback in `tests/ux-scenarios/test_us3_interruption_fallback.md`
- [ ] T042 [P] [US3] Add privacy-and-policy validation for cloud disclosure and no-store defaults in `tests/contract/test_us3_privacy_policy_contract.kt`

### Implementation for User Story 3

- [ ] T043 [P] [US3] Implement network-quality monitor and recognition-path fallback policy in `android/data/network/src/main/kotlin/com/example/voiceinput/data/network/NetworkQualityMonitor.kt`
- [ ] T044 [P] [US3] Implement recognition failure handler and user-retry decision logic in `android/ime/src/main/kotlin/com/example/voiceinput/session/RecognitionFailureHandler.kt`
- [ ] T045 [US3] Implement accidental-trigger cancel and fast-exit behavior in `android/ime/src/main/kotlin/com/example/voiceinput/capture/AccidentalTriggerGuard.kt`
- [ ] T046 [US3] Implement focus-loss, app-switch, and manual-keyboard fallback coordinator in `android/ime/src/main/kotlin/com/example/voiceinput/session/FallbackTransitionCoordinator.kt`
- [ ] T047 [US3] Implement cloud-processing disclosure UI and policy-boundary messaging in `android/ime/src/main/kotlin/com/example/voiceinput/ui/CloudDisclosureBanner.kt`
- [ ] T048 [US3] Implement privacy record persistence and raw-audio transient cleanup in `android/core/privacy/src/main/kotlin/com/example/voiceinput/privacy/TransientAudioCleanupManager.kt`

**Checkpoint**: All user stories should now be independently functional, including degraded-network and interruption recovery

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Finalize documentation, hardening, and scenario validation that cuts across all user stories

- [ ] T049 [P] Document implementation architecture and module responsibilities in `docs/voice-input-mvp/architecture.md`
- [ ] T050 Refine session state naming, remove dead paths, and clean up shared abstractions in `android/ime/src/main/kotlin/com/example/voiceinput/` and `android/core/model/src/main/kotlin/com/example/voiceinput/model/`
- [ ] T051 [P] Tune performance budgets and benchmark thresholds across the suite in `tests/performance/` and `android/benchmark/`
- [ ] T052 [P] Add additional unit coverage for reducers, policy gates, and punctuation logic in `android/ime/src/test/kotlin/com/example/voiceinput/` and `android/core/src/test/kotlin/com/example/voiceinput/`
- [ ] T053 Harden privacy, data-retention, and disclosure handling against edge cases in `android/core/privacy/src/main/kotlin/com/example/voiceinput/privacy/` and `backend/recognition-gateway/policy/`
- [ ] T054 Validate task completion time, edit-count improvement, and repeat-use willingness against baseline scenarios in `tests/ux-scenarios/quickstart-validation-report.md`
- [ ] T055 Audit cloud upload boundaries, no-store defaults, and user-control behavior in `docs/voice-input-mvp/privacy-audit.md`
- [ ] T056 Run the full quickstart scenario validation and record outcomes in `specs/001-voice-input-mvp/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion; blocks all user stories.
- **User Story 1 (Phase 3)**: Depends on Foundational completion; defines the MVP main path.
- **User Story 2 (Phase 4)**: Depends on User Story 1 session insertion flow and Foundational completion because correction builds on accepted-text insertion and continuation.
- **User Story 3 (Phase 5)**: Depends on Foundational completion and is safest after User Story 1 because fallback logic needs the primary session loop in place.
- **Polish (Phase 6)**: Depends on all targeted user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational; no dependency on other stories.
- **User Story 2 (P2)**: Depends on US1’s insertion and continuation path but should remain independently testable once implemented.
- **User Story 3 (P3)**: Depends on US1’s session orchestration and shared privacy/network foundations; it may coexist with US2 but should remain independently testable.

### Within Each User Story

- Contract, integration, UX, and performance validations MUST be added before or alongside implementation for the affected behavior.
- Shared domain or persistence helpers before service/coordinator wiring.
- Session and service logic before UI integration where the UI depends on state transitions.
- Story-specific telemetry before final validation so metrics are observable during testing.

### Parallel Opportunities

- `T003`-`T005` can run in parallel during setup.
- `T007`, `T008`, `T010`, `T011`, and `T014` can run in parallel after the directory and build scaffolding is in place.
- Within US1, tests `T016`-`T020` can run in parallel, and UI/rendering tasks `T021`-`T022` can run in parallel.
- Within US2, tests `T028`-`T031` can run in parallel, and tasks `T032`-`T033` can run in parallel.
- Within US3, tests `T038`-`T042` can run in parallel, and tasks `T043`-`T045` can run in parallel.
- In Phase 6, `T049`, `T051`, `T052`, `T054`, and `T055` can run in parallel.

---

## Parallel Example: User Story 1

```bash
# Launch validation work for User Story 1 together:
Task: "Add contract verification for UI session states and entry-point guarantees in tests/contract/test_input_method_ui_contract.kt"
Task: "Add integration test for chat reply transcription and insertion flow in tests/integration/test_us1_chat_reply_flow.kt"
Task: "Add performance benchmark for first-feedback latency and utterance finalization in tests/performance/test_us1_latency_benchmark.kt"

# Launch independent implementation slices for User Story 1 together:
Task: "Implement the voice-input trigger surface and listening-state UI in android/ime/src/main/kotlin/com/example/voiceinput/ui/VoiceInputEntryView.kt"
Task: "Implement partial and final transcript rendering into the active text field in android/ime/src/main/kotlin/com/example/voiceinput/ui/ActiveFieldRenderer.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup.
2. Complete Phase 2: Foundational.
3. Complete Phase 3: User Story 1.
4. Validate chat and search scenarios plus first-feedback latency.
5. Demo the Android-first invoke-transcribe-insert-continue flow.

### Incremental Delivery

1. Complete Setup and Foundational once.
2. Deliver User Story 1 as the MVP main path.
3. Add User Story 2 to reduce correction friction and enable continuation after edits.
4. Add User Story 3 to harden fallback, privacy disclosure, and degraded-network behavior.
5. Finish with cross-cutting performance, privacy, and validation polish.

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup and Foundational together.
2. Once Foundational is done, one developer owns US1 flow and latency instrumentation.
3. A second developer can prepare US2 correction and punctuation tasks after US1 interfaces stabilize.
4. A third developer can prepare US3 network, privacy, and fallback tests against the shared contracts.

---

## Notes

- [P] tasks = different files, no dependencies on incomplete work.
- [US1], [US2], and [US3] map directly to the prioritized stories in `spec.md`.
- Each user story phase is independently testable against the criteria defined above.
- The suggested MVP scope is Phase 3 only: User Story 1.
- Avoid adding assistant-style features, multilingual support, or heavy personalization work in this task list.
