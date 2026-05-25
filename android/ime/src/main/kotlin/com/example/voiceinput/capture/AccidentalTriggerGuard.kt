package com.example.voiceinput.capture

class AccidentalTriggerGuard {
    fun shouldCommit(transcribedText: String, cancelled: Boolean): Boolean {
        return !cancelled && transcribedText.isNotBlank()
    }
}

