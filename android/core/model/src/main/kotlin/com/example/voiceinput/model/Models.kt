package com.example.voiceinput.model

enum class EntryContext { CHAT, SEARCH, FORM, MEMO }
enum class SessionState { IDLE, LISTENING, TRANSCRIBING_LOCAL, TRANSCRIBING_CLOUD, AWAITING_USER_EDIT, COMPLETED, CANCELLED, FAILED }
enum class NetworkMode { OFFLINE, WEAK, NORMAL, STRONG }
enum class FinalOutcome { ACCEPTED, EDITED, RETRIED, ABANDONED, FALLBACK_TO_KEYBOARD }
enum class InterruptionReason { NONE, USER_CANCEL, APP_SWITCH, TIMEOUT, RECOGNITION_ERROR, NETWORK_ERROR, ACCIDENTAL_TRIGGER }
enum class PunctuationMode { AUTO, OFF, USER_ADJUSTED }
enum class ConfidenceBand { LOW, MEDIUM, HIGH }
enum class RecognitionSource { LOCAL, CLOUD }
enum class SegmentState { CAPTURING, PARTIAL_READY, FINAL_READY, CORRECTED, REVERTED, DISCARDED }
enum class FieldType { CHAT_BOX, SEARCH_BOX, SINGLE_LINE_FORM, MULTI_LINE_FORM, MEMO_BOX }
enum class FieldFocusState { ACTIVE, BACKGROUNDED, LOST }
enum class CorrectionActionType { MANUAL_EDIT, DELETE, UNDO_LAST_SEGMENT, RETRY, PUNCTUATION_TOGGLE }
enum class ActionTarget { CHARACTER_RANGE, WHOLE_SEGMENT, SESSION_SETTING }
enum class PostActionResult { RESUMED_VOICE, SWITCHED_KEYBOARD, ACCEPTED_TEXT, CANCELLED }
enum class AudioRetentionMode { TRANSIENT_ONLY, USER_CONSENTED_DEBUG }
enum class TranscriptRetentionMode { NONE, SUMMARY_ONLY, LOCAL_SHORT_TERM }
enum class CloudTransferMode { NONE, PARTIAL_AUDIO, ENCODED_FEATURES, TRANSCRIPT_ONLY }

data class VoiceInputSession(
    val sessionId: String,
    val platform: String = "Android",
    val entryContext: EntryContext,
    val sessionState: SessionState,
    val networkMode: NetworkMode,
    val cloudEnhancementUsed: Boolean,
    val acceptedTextLength: Int = 0,
    val manualCorrectionCount: Int = 0,
    val undoCount: Int = 0,
    val finalOutcome: FinalOutcome? = null,
    val interruptionReason: InterruptionReason = InterruptionReason.NONE,
)

data class UtteranceSegment(
    val segmentId: String,
    val sessionId: String,
    val sequenceNumber: Int,
    val partialText: String = "",
    val finalText: String = "",
    val punctuationMode: PunctuationMode = PunctuationMode.AUTO,
    val confidenceBand: ConfidenceBand = ConfidenceBand.MEDIUM,
    val recognitionSource: RecognitionSource = RecognitionSource.LOCAL,
    val segmentState: SegmentState = SegmentState.CAPTURING,
    val insertStartCursor: Int = 0,
    val insertEndCursor: Int = 0,
)

data class TextFieldContext(
    val contextId: String,
    val sessionId: String,
    val fieldType: FieldType,
    val cursorPosition: Int,
    val selectionRange: IntRange,
    val keyboardFallbackAvailable: Boolean = true,
    val continuationAllowed: Boolean = true,
    val fieldFocusState: FieldFocusState = FieldFocusState.ACTIVE,
)

data class CorrectionAction(
    val correctionId: String,
    val sessionId: String,
    val segmentId: String?,
    val actionType: CorrectionActionType,
    val actionTarget: ActionTarget,
    val postActionResult: PostActionResult,
)

data class PersonalizationProfile(
    val profileId: String,
    val token: String,
    val contextBucket: String,
    val acceptCount: Int = 0,
    val correctionToCount: Int = 0,
)

data class PrivacyPolicyRecord(
    val recordId: String,
    val sessionId: String,
    val audioRetentionMode: AudioRetentionMode = AudioRetentionMode.TRANSIENT_ONLY,
    val transcriptRetentionMode: TranscriptRetentionMode = TranscriptRetentionMode.SUMMARY_ONLY,
    val cloudTransferMode: CloudTransferMode = CloudTransferMode.NONE,
    val userDisclosureShown: Boolean = false,
    val policyVersion: String = "1.0",
)

