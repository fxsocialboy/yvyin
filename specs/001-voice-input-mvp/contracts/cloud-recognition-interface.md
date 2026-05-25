# Contract: Cloud Recognition Enhancement Interface

## Purpose

Define the minimum contract between the Android client and the cloud enhancement path
used only when network quality and policy checks allow it.

## Invocation Preconditions

- The local session is already active and has produced a baseline transcript or is
  actively streaming audio features.
- Network mode is `normal` or `strong`.
- User disclosure for cloud processing has been shown.
- The session policy allows cloud enhancement for the current context.

## Request Contract

### RecognitionEnhancementRequest

- `session_id`: unique session identifier
- `segment_id`: unique segment identifier
- `locale`: fixed to `zh-CN` in MVP
- `context_type`: `chat`, `search`, `form`, or `memo`
- `audio_payload_type`: `partial_audio` or `encoded_features`
- `audio_payload`: transient segment content needed for enhancement
- `baseline_partial_text`: optional local hypothesis
- `punctuation_mode`: `auto` or `off`
- `network_quality`: `normal` or `strong`
- `client_timestamp`

## Response Contract

### RecognitionEnhancementResponse

- `session_id`
- `segment_id`
- `enhanced_text`
- `confidence_band`: `low`, `medium`, `high`
- `punctuation_applied`: boolean
- `replacement_mode`: `append_safe` or `replace_segment`
- `response_timestamp`
- `policy_flags`: zero or more of `no_store`, `summary_only`, `user_disclosed`

## Behavioral Rules

- The enhancement path must never require the client to discard accepted text outside
  the current segment boundary.
- If the cloud response arrives too late for the active UX budget, the client may
  ignore it and keep the local result.
- The enhancement path must honor the default no-long-term-raw-audio-retention policy.
- The interface must support a no-store processing mode as the default.

## Failure Modes

| Failure mode | Required client behavior |
|--------------|--------------------------|
| Timeout | Keep local transcript, allow edit or retry |
| Network drop | Preserve accepted text and fall back to local-only mode |
| Low-confidence response | Prefer local result or request user correction |
| Policy rejection | Skip cloud enhancement and continue with local path |

## Observability Signals

- `cloud_request_attempted`
- `cloud_request_succeeded`
- `cloud_request_timed_out`
- `cloud_request_rejected_by_policy`
- `cloud_response_ignored_for_latency_budget`
