package com.example.voiceinput.capture

import com.example.voiceinput.model.EntryContext
import com.example.voiceinput.model.NetworkMode
import com.example.voiceinput.model.SessionState
import com.example.voiceinput.model.InterruptionReason
import com.example.voiceinput.model.VoiceInputSession
import com.example.voiceinput.session.VoiceInputSessionManager

class VoiceCaptureCoordinator(
    private val sessionManager: VoiceInputSessionManager,
) {
    fun begin(sessionId: String, entryContext: EntryContext): VoiceInputSession {
        return sessionManager.transition(
            VoiceInputSession(
                sessionId = sessionId,
                entryContext = entryContext,
                sessionState = SessionState.IDLE,
                networkMode = NetworkMode.NORMAL,
                cloudEnhancementUsed = false,
            ),
            SessionState.LISTENING,
        )
    }

    fun cancel(session: VoiceInputSession): VoiceInputSession {
        return sessionManager.transition(
            session.copy(interruptionReason = InterruptionReason.USER_CANCEL),
            SessionState.CANCELLED,
        )
    }

    fun awaitingEdit(session: VoiceInputSession): VoiceInputSession {
        return sessionManager.transition(session, SessionState.AWAITING_USER_EDIT)
    }

    fun complete(session: VoiceInputSession, acceptedTextLength: Int): VoiceInputSession {
        val transcribing = when (session.sessionState) {
            SessionState.LISTENING -> sessionManager.transition(session, SessionState.TRANSCRIBING_LOCAL)
            SessionState.TRANSCRIBING_LOCAL,
            SessionState.TRANSCRIBING_CLOUD,
            SessionState.AWAITING_USER_EDIT,
            SessionState.COMPLETED,
            SessionState.CANCELLED,
            SessionState.FAILED,
            SessionState.IDLE,
            -> session
        }
        return sessionManager.transition(
            transcribing.copy(acceptedTextLength = acceptedTextLength),
            SessionState.AWAITING_USER_EDIT,
        ).let { awaiting ->
            sessionManager.transition(awaiting, SessionState.COMPLETED)
        }
    }

    fun fail(session: VoiceInputSession, reason: InterruptionReason): VoiceInputSession {
        return sessionManager.transition(
            session.copy(interruptionReason = reason),
            SessionState.FAILED,
        )
    }
}
