<!--
Sync Impact Report
- Version change: template -> 1.0.0
- Modified principles:
  - Principle slot 1 -> I. Efficiency First
  - Principle slot 2 -> II. Accuracy Is a Product Baseline
  - Principle slot 3 -> III. Low Latency Over Perfect Post-Processing
  - Principle slot 4 -> IV. Simplicity of Interaction
  - Principle slot 5 -> V. Cost Must Be Sustainable
  - added VI. Privacy and User Trust
  - added VII. Fallback and Error Recovery
  - added VIII. Validate With Real User Scenarios
- Added sections:
  - Product Scope and Delivery Boundaries
  - Delivery and Review Workflow
- Removed sections:
  - None
- Templates requiring updates:
  - ✅ updated .specify/templates/plan-template.md
  - ✅ updated .specify/templates/spec-template.md
  - ✅ updated .specify/templates/tasks-template.md
  - ⚠ pending .specify/templates/commands/*.md (directory not present)
  - ⚠ pending runtime guidance docs such as README.md or docs/quickstart.md (not present)
- Follow-up TODOs:
  - None
-->
# 语音输入法产品 Constitution

## Core Principles

### I. Efficiency First
Every feature MUST produce a concrete improvement in input-task completion speed or
editing effort for a real user scenario. Product work MUST prioritize high-frequency
flows such as chat replies, search queries, short-note capture, and form filling.
Features that add interaction steps without a measurable efficiency gain MUST NOT be
prioritized for MVP or near-term delivery.

Rationale: Voice input is only valuable when it reduces total work, not when it
adds novelty without shortening the path to finished text.

### II. Accuracy Is a Product Baseline
Baseline recognition quality for common Mandarin usage is non-negotiable. Specs and
plans MUST define how the feature handles punctuation, numbers, proper nouns,
hesitations, and other sources of downstream editing cost. Evaluation MUST include
user modification effort in addition to raw recognition error metrics.

Rationale: A fast transcription that requires heavy cleanup erases the advantage of
voice input.

### III. Low Latency Over Perfect Post-Processing
Primary flows MUST favor low-latency feedback so users can see text appear during or
immediately after speaking. Post-processing, re-ranking, and correction stages MUST
stay within a user-tolerable delay budget and MUST be justified when they add
latency. If an accuracy improvement is marginal, the faster feedback path MUST win.

Rationale: Input methods are interaction surfaces; perceived responsiveness directly
determines whether users continue using them.

### IV. Simplicity of Interaction
The core interaction MUST remain lightweight: tap or auto-listen, real-time or
near-real-time text insertion, then fast confirm or edit. Complex command grammars
MUST NOT become the primary path for common tasks. Key capabilities MUST remain
learnable by first-time users without training.

Rationale: An input method competes with the keyboard, so any added cognitive load is
product debt.

### V. Cost Must Be Sustainable
Every architecture decision MUST state whether processing is on-device, cloud, or
hybrid and MUST document expected unit cost, resource consumption, and scaling
limits. High-cost capabilities MUST be reserved for scenarios with explicit value.
Designs that cannot support the current stage of rollout and growth MUST NOT pass
planning review.

Rationale: Unsustainable inference or bandwidth costs will block iteration and
distribution even if the feature works technically.

### VI. Privacy and User Trust
The system MUST follow data minimization by default. Specs MUST state which data is
processed locally, which data is uploaded, and how recordings, transcripts, and user
dictionaries are retained, deleted, and user-controlled. Sensitive audio or text MUST
NOT be collected or stored without a clear purpose and lifecycle.

Rationale: Voice input handles high-sensitivity content, so trust is a product
requirement rather than a legal afterthought.

### VII. Fallback and Error Recovery
Every voice-input flow MUST include a smooth recovery path for recognition failure,
network degradation, accidental trigger, and user correction. Manual editing,
rollback, and retry mechanisms MUST be treated as first-class behavior in the main
flow instead of polish work.

Rationale: Voice recognition failures are inevitable; the product wins when users can
recover without losing momentum.

### VIII. Validate With Real User Scenarios
Each release MUST identify target users, the concrete input task being improved, and
success criteria based on real task completion. Validation MUST prioritize total time
to finish an input task, number of edits, and completion success over abstract model
benchmarks alone. Research findings and gray-release feedback MUST directly influence
priority decisions.

Rationale: Product quality is determined by whether users finish real tasks faster
with acceptable effort.

## Product Scope and Delivery Boundaries

This constitution applies to a voice input product focused on efficient text entry in
chat, search, short writing, and form scenarios. The current MVP scope MUST center on
Mandarin speech-to-text, real-time or near-real-time text insertion, basic punctuation
and segmentation optimization, simple correction and undo paths, and smooth transition
between confirming text and continuing input.

The following are explicitly not first-order goals for the current stage: building a
general-purpose voice assistant, delivering complex multi-turn AI dialogue, building a
heavy personalization stack before validation, or sacrificing core-path speed for
low-frequency advanced features. Overly complex voice command systems, broad
multilingual expansion, and heavyweight cloud training remain deferred until real user
evidence justifies them.

## Delivery and Review Workflow

All specifications MUST answer the following questions before planning starts:

1. Who is the target user and what input task is being improved?
2. What is the current pain point and why is voice the right intervention?
3. How does the feature improve input efficiency?
4. What are the concrete requirements for accuracy, latency, and cost?
5. How does the user recover when recognition or networking fails?
6. How will the team prove the feature is better than the current alternative?

When solution options conflict, reviewers MUST evaluate them in this order:

1. Whether the user completes the input task faster.
2. Whether the user reaches an acceptable final result with fewer edits.
3. Whether the interaction remains real-time and smooth.
4. Whether the operating cost supports the current product stage.
5. Whether privacy and data handling remain acceptable.

Plans MUST fail constitution review if they omit measurable latency, accuracy, cost,
privacy, or fallback considerations for a voice-input user flow. Tasks MUST include
work required to validate those constraints whenever they are affected by the feature.

## Governance

This constitution overrides conflicting local habits, draft templates, and feature
preferences. Amendments MUST be documented in the constitution itself, include the
reason for change, and trigger a consistency review of dependent templates and
guidance files before adoption.

Versioning policy:

- MAJOR versions change or remove a core principle or governance rule in a way that
  invalidates previously compliant plans.
- MINOR versions add a new principle, section, or materially expanded requirement.
- PATCH versions clarify wording, improve examples, or make non-semantic refinements.

Compliance review expectations:

- Every specification review MUST check target user, task, efficiency claim, quality
  constraints, fallback path, and validation method against this constitution.
- Every implementation plan MUST pass the constitution gate before research ends and
  again after design is completed.
- Every task list MUST map implementation and validation work back to user stories and
  include any required measurement, privacy, and recovery tasks.

**Version**: 1.0.0 | **Ratified**: 2026-05-25 | **Last Amended**: 2026-05-25
