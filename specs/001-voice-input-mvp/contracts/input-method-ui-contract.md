# Contract: Input Method UI Behavior

## Purpose

Define the user-visible contract for the Android-first voice input main flow so that
implementation, QA, and product validation use the same behavior model.

## Entry Points

- A single obvious voice-input trigger is available from the active input surface.
- Triggering voice input MUST not remove existing accepted text.
- Triggering voice input MUST reveal a listening state immediately.

## Required Session States

| State | User-visible meaning | Required behavior |
|-------|----------------------|-------------------|
| `listening` | The microphone is actively capturing | Show active listening indicator and allow immediate cancel |
| `transcribing_local` | Baseline recognition is producing text | Insert partial text as soon as available |
| `transcribing_cloud` | Optional enhancement is active | Keep already visible text stable and avoid blocking manual edit |
| `awaiting_user_edit` | Text is in field and editable | Allow correction, undo, resume speaking, or keyboard fallback |
| `failed` | Recognition could not complete acceptably | Preserve accepted text, show retry/fallback options |
| `cancelled` | User intentionally stopped the flow | Exit quickly without unintended insertion |

## Main-Flow Guarantees

- Users can speak and see text appear in real time or near real time.
- Users can continue input after a finalized segment without re-entering setup.
- Users can edit inside the active text field instead of moving to a separate
  composition screen.
- Users can undo the latest inserted segment in one step.
- Users can switch back to manual keyboard input at any point.

## Punctuation Contract

- Automatic punctuation is on by default in MVP.
- Users can correct punctuation inline.
- Users can disable punctuation assistance for the active entry session without
  navigating away from the task.

## Failure and Recovery Contract

- Network degradation MUST NOT erase accepted text already inserted in the field.
- If cloud enhancement is unavailable, the UI MUST continue with the local baseline or
  prompt quick retry without trapping the user.
- Accidental trigger MUST be reversible before unwanted text is committed.
- Interruption by app switch or focus loss MUST leave the session in a recoverable or
  safely terminated state.

## Privacy Disclosure Contract

- If cloud enhancement is used, the user must have a clear indication that speech data
  is being sent for cloud processing.
- The UI must not imply long-term raw-audio storage when the default policy is
  transient processing only.
