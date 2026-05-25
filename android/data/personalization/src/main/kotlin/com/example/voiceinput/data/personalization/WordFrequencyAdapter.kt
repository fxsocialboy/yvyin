package com.example.voiceinput.data.personalization

import com.example.voiceinput.model.PersonalizationProfile

class WordFrequencyAdapter {
    fun adapt(existing: PersonalizationProfile?, token: String, contextBucket: String): PersonalizationProfile {
        return if (existing == null) {
            PersonalizationProfile("profile-$token", token, contextBucket, acceptCount = 1)
        } else {
            existing.copy(acceptCount = existing.acceptCount + 1)
        }
    }
}

