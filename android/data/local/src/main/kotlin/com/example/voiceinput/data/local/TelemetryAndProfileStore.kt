package com.example.voiceinput.data.local

import com.example.voiceinput.model.CorrectionAction
import com.example.voiceinput.model.PersonalizationProfile
import com.example.voiceinput.model.PrivacyPolicyRecord
import com.example.voiceinput.model.VoiceInputSession

class SessionTelemetryDao {
    private val sessions = mutableListOf<VoiceInputSession>()
    fun upsert(session: VoiceInputSession) {
        sessions.removeAll { it.sessionId == session.sessionId }
        sessions += session
    }
    fun all(): List<VoiceInputSession> = sessions.toList()
}

class PersonalizationProfileDao {
    private val profiles = mutableMapOf<String, PersonalizationProfile>()
    fun upsert(profile: PersonalizationProfile) {
        profiles[profile.token] = profile
    }
    fun find(token: String): PersonalizationProfile? = profiles[token]
}

class PrivacyPolicyRecordDao {
    private val records = mutableListOf<PrivacyPolicyRecord>()
    fun insert(record: PrivacyPolicyRecord) {
        records += record
    }
    fun all(): List<PrivacyPolicyRecord> = records.toList()
}

class CorrectionActionDao {
    private val actions = mutableListOf<CorrectionAction>()
    fun insert(action: CorrectionAction) {
        actions += action
    }
    fun bySession(sessionId: String): List<CorrectionAction> = actions.filter { it.sessionId == sessionId }
}

