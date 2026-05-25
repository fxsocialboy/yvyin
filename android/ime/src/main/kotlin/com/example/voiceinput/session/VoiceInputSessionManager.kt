package com.example.voiceinput.session

import com.example.voiceinput.model.SessionState
import com.example.voiceinput.model.VoiceInputSession

class VoiceInputSessionManager {
    fun transition(session: VoiceInputSession, nextState: SessionState): VoiceInputSession {
        val allowed = when (session.sessionState) {
            SessionState.IDLE -> setOf(SessionState.LISTENING)
            SessionState.LISTENING -> setOf(SessionState.TRANSCRIBING_LOCAL, SessionState.CANCELLED, SessionState.FAILED)
            SessionState.TRANSCRIBING_LOCAL -> setOf(SessionState.TRANSCRIBING_CLOUD, SessionState.AWAITING_USER_EDIT, SessionState.FAILED)
            SessionState.TRANSCRIBING_CLOUD -> setOf(SessionState.AWAITING_USER_EDIT, SessionState.FAILED)
            SessionState.AWAITING_USER_EDIT -> setOf(SessionState.COMPLETED, SessionState.LISTENING, SessionState.CANCELLED)
            SessionState.COMPLETED, SessionState.CANCELLED, SessionState.FAILED -> emptySet()
        }
        require(nextState in allowed) { "Invalid session transition: ${session.sessionState} -> $nextState" }
        return session.copy(sessionState = nextState)
    }
}

