# Data Model: Voice Input MVP

## VoiceInputSession

- **Purpose**: Represents one invoked voice-input interaction from capture start to
  completion, cancellation, or failure.
- **Fields**:
  - `session_id`: unique identifier
  - `platform`: fixed to Android for MVP
  - `entry_context`: chat, search, form, memo
  - `session_state`: idle, listening, transcribing_local, transcribing_cloud,
    awaiting_user_edit, completed, cancelled, failed
  - `network_mode`: offline, weak, normal, strong
  - `cloud_enhancement_used`: boolean
  - `started_at`
  - `ended_at`
  - `final_outcome`: accepted, edited, retried, abandoned, fallback_to_keyboard
  - `accepted_text_length`
  - `manual_correction_count`
  - `undo_count`
  - `interruption_reason`: none, user_cancel, app_switch, timeout, recognition_error,
    network_error, accidental_trigger
- **Validation rules**:
  - `session_id` must be unique
  - `entry_context` must be one of the supported MVP scenarios
  - `ended_at` must be present when `session_state` is terminal
- **State transitions**:
  - `idle -> listening`
  - `listening -> transcribing_local`
  - `transcribing_local -> transcribing_cloud` when network and policy allow
  - `transcribing_local|transcribing_cloud -> awaiting_user_edit`
  - `awaiting_user_edit -> completed|listening|cancelled`
  - any active state -> failed|cancelled

## UtteranceSegment

- **Purpose**: Tracks a single spoken segment within a session and its transcription
  lifecycle.
- **Fields**:
  - `segment_id`
  - `session_id`
  - `sequence_number`
  - `audio_window_ms`
  - `partial_text`
  - `final_text`
  - `punctuation_mode`: auto, off, user_adjusted
  - `confidence_band`: low, medium, high
  - `recognition_source`: local, cloud
  - `segment_state`: capturing, partial_ready, final_ready, corrected, reverted,
    discarded
  - `insert_start_cursor`
  - `insert_end_cursor`
- **Validation rules**:
  - `sequence_number` must be increasing within a session
  - `final_text` required before `segment_state` enters `final_ready`
  - cursor bounds must fit the target field state

## TextFieldContext

- **Purpose**: Represents the active text surface that receives inserted speech.
- **Fields**:
  - `context_id`
  - `session_id`
  - `field_type`: chat_box, search_box, single_line_form, multi_line_form, memo_box
  - `cursor_position`
  - `selection_range`
  - `keyboard_fallback_available`: boolean
  - `continuation_allowed`: boolean
  - `field_focus_state`: active, backgrounded, lost
- **Validation rules**:
  - `continuation_allowed` can only be true when focus is active
  - selection range must be valid within current text length

## CorrectionAction

- **Purpose**: Captures user edits that quantify cleanup cost and recovery friction.
- **Fields**:
  - `correction_id`
  - `session_id`
  - `segment_id`
  - `action_type`: manual_edit, delete, undo_last_segment, retry, punctuation_toggle
  - `action_target`: character_range, whole_segment, session_setting
  - `triggered_at`
  - `post_action_result`: resumed_voice, switched_keyboard, accepted_text, cancelled
- **Validation rules**:
  - `action_type` must map to an allowed post-action result
  - `segment_id` may be null only for session-level setting changes

## PersonalizationProfile

- **Purpose**: Stores bounded local adaptation data for repeated user vocabulary.
- **Fields**:
  - `profile_id`
  - `token`
  - `context_bucket`: chat, search, form, memo, global
  - `accept_count`
  - `correction_to_count`
  - `last_used_at`
  - `source`: accepted_text, manual_correction
- **Validation rules**:
  - stored token count must remain within local quota
  - tokens derived from explicitly deleted sensitive content must be removable

## PrivacyPolicyRecord

- **Purpose**: Records what data-handling mode applied to a session for auditing and
  UX disclosure.
- **Fields**:
  - `record_id`
  - `session_id`
  - `audio_retention_mode`: transient_only, user_consented_debug
  - `transcript_retention_mode`: none, summary_only, local_short_term
  - `cloud_transfer_mode`: none, partial_audio, encoded_features, transcript_only
  - `user_disclosure_shown`: boolean
  - `policy_version`
- **Validation rules**:
  - `user_disclosure_shown` must be true whenever `cloud_transfer_mode` is not `none`
  - retention modes must match active privacy policy version

## Relationships

- One `VoiceInputSession` has many `UtteranceSegment` records.
- One `VoiceInputSession` has one active `TextFieldContext` at a time.
- One `VoiceInputSession` can have many `CorrectionAction` records.
- One `VoiceInputSession` has one `PrivacyPolicyRecord`.
- `PersonalizationProfile` aggregates accepted tokens across many sessions but stays
  local to the device in MVP.
