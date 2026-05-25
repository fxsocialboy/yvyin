# Research: Voice Input MVP

## Decision 1: Android-first IME architecture

- **Decision**: Build the MVP as an Android-first input-method implementation using
  native Android IME capabilities and a dedicated voice-input session layer.
- **Rationale**: The spec explicitly prioritizes Android before iOS. Android IME
  integration gives direct control over invocation, active text-field insertion,
  correction continuity, and keyboard fallback, which are all critical to the main
  voice-input loop.
- **Alternatives considered**:
  - Cross-platform shell first: rejected because platform abstraction would slow down
    Android-first interaction optimization.
  - App-only dictation surface: rejected because it breaks the requirement to insert
    text directly into the active field.

## Decision 2: Hybrid recognition with local baseline and selective cloud enhancement

- **Decision**: Use a hybrid recognition strategy: an on-device baseline handles
  capture start, immediate feedback, and weak-network fallback; cloud enhancement is
  invoked only when network quality and cost policy permit.
- **Rationale**: This is the only approach that satisfies the constitution’s
  latency-first, fallback, and sustainable-cost requirements simultaneously.
- **Alternatives considered**:
  - Pure cloud recognition: rejected because network instability would break the core
    user flow and increase perceived latency.
  - Pure on-device recognition: rejected because proper noun handling, punctuation,
    and overall accuracy would likely lag too far behind user expectations in MVP.

## Decision 3: Lightweight correction instead of full-text rewrite

- **Decision**: Keep correction limited to in-field manual edits, latest-segment undo,
  punctuation adjustment, and continuation from the current cursor position.
- **Rationale**: The core value is faster completion for short text. Full-document
  rewrite or assistant-style correction would add latency, interaction complexity, and
  unpredictable behavior.
- **Alternatives considered**:
  - Full-utterance rewrite on every correction: rejected because it risks overwriting
    accepted text and increases user distrust.
  - Command-heavy correction grammar: rejected because it adds learning cost and does
    not match the MVP simplicity principle.

## Decision 4: Data minimization with no default long-term raw-audio retention

- **Decision**: Do not persist raw audio by default beyond transient in-flight
  processing; keep only the minimum telemetry and local adaptation data needed for the
  feature and evaluation.
- **Rationale**: The product handles sensitive user content. Trust and privacy are
  constitutional requirements, not optional enhancements.
- **Alternatives considered**:
  - Retain raw audio for offline training by default: rejected because it increases
    trust risk and storage exposure.
  - Persist full transcripts for all sessions: rejected because many validation goals
    can be met with summary telemetry instead.

## Decision 5: Basic local personalization only

- **Decision**: Limit personalization to local word-frequency adaptation and recent
  accepted corrections that can bias future ranking, without training a heavy
  user-specific model.
- **Rationale**: This improves repeated short-text input while keeping cost, privacy,
  and product complexity bounded for MVP.
- **Alternatives considered**:
  - No personalization at all: rejected because repeated names and phrases are common
    in chat and form scenarios.
  - Heavy cloud personalization training: rejected because it conflicts with MVP cost
    and privacy boundaries.

## Decision 6: Performance and validation budgets

- **Decision**: Optimize for first visible transcription feedback within 300 ms median
  after speech onset and finalized short utterance within 800 ms median on good
  networks, while proving at least 25% faster task completion than keyboard-only
  input.
- **Rationale**: The product wins only if users feel it is faster in real tasks, not
  just if component metrics look good in isolation.
- **Alternatives considered**:
  - Accuracy-first with slower finalization: rejected because delayed feedback harms
    interaction confidence.
  - No explicit latency target until later: rejected because planning without budgets
    would violate the constitution’s quality-gate requirements.
