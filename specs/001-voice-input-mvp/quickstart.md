# Quickstart: Validate Voice Input MVP Plan

## Purpose

Provide a scenario-based validation script for the planned MVP before implementation
tasks are generated.

## Preconditions

- The test device is an Android 12+ phone configured with the MVP voice input method.
- A baseline mobile keyboard path is available for comparison.
- Test scenarios cover chat, search, form entry, and short memo entry.
- Network conditions can be toggled between strong, weak, and offline.

## Scenario 1: Chat reply speed

1. Open a supported chat input field.
2. Trigger voice input with one obvious action.
3. Speak a short Mandarin reply of 8-15 characters.
4. Confirm that visible text appears during speech or immediately after it.
5. Measure completion time and number of manual edits.
6. Repeat with keyboard-only input for comparison.

**Expected outcome**: Voice path is measurably faster on median and does not require
heavy cleanup.

## Scenario 2: Search query with pause

1. Open a search field.
2. Trigger voice input and speak a short query with a natural pause.
3. Verify that the resulting query remains usable without complex commands.
4. Toggle punctuation assistance off if the query format requires it.

**Expected outcome**: The user gets a usable query string quickly and can adjust
punctuation without leaving the flow.

## Scenario 3: Form entry with correction

1. Open a short form field such as notes or description.
2. Speak a short Mandarin phrase that includes a number or proper noun.
3. Introduce a controlled recognition mistake.
4. Correct the text inline and continue speaking.

**Expected outcome**: The correction does not clear the existing field, and resumed
speech appends from the current cursor position.

## Scenario 4: Weak-network fallback

1. Start a voice-input session under normal network conditions.
2. Degrade network quality mid-utterance.
3. Verify that accepted text remains in the field.
4. Confirm the session falls back to local-only behavior, retry, or keyboard fallback
   without trapping the user.

**Expected outcome**: No accepted text is lost and the user can finish the task.

## Scenario 5: Accidental trigger and cancel

1. Trigger voice input unintentionally.
2. Cancel immediately before meaningful speech is processed.
3. Confirm that no unwanted text is inserted.

**Expected outcome**: The user exits quickly and safely.

## Metrics to Capture

- Task completion time
- Manual correction count
- Undo count
- Fallback-to-keyboard rate
- Recognition acceptable-rate
- First visible transcription feedback time
- Finalization time for short utterances
- Repeat-use willingness after scenario completion

## Exit Criteria

- Main flow works for chat, search, form, and memo scenarios.
- Hybrid recognition behavior preserves usability under weak network conditions.
- Privacy disclosure is visible whenever cloud enhancement is invoked.
- All primary metrics can be observed or instrumented before task breakdown starts.
