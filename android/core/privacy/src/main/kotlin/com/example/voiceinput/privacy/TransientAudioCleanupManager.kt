package com.example.voiceinput.privacy

class TransientAudioCleanupManager {
    fun clear(buffer: ByteArray?): Int {
        return buffer?.size ?: 0
    }
}

