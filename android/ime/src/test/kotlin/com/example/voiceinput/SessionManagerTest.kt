package com.example.voiceinput

import com.example.voiceinput.model.EntryContext
import com.example.voiceinput.model.NetworkMode
import com.example.voiceinput.model.SessionState
import com.example.voiceinput.model.VoiceInputSession
import com.example.voiceinput.session.VoiceInputSessionManager
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionManagerTest {
    @Test
    fun allowsIdleToListeningTransition() {
        val manager = VoiceInputSessionManager()
        val session = VoiceInputSession("s1", entryContext = EntryContext.CHAT, sessionState = SessionState.IDLE, networkMode = NetworkMode.NORMAL, cloudEnhancementUsed = false)
        assertEquals(SessionState.LISTENING, manager.transition(session, SessionState.LISTENING).sessionState)
    }
}

