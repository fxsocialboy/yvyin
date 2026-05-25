package com.example.voiceinput.session

class RecognitionFailureHandler {
    fun nextAction(lowConfidence: Boolean, existingText: String): String {
        return when {
            existingText.isNotBlank() -> "fallback_to_edit"
            lowConfidence -> "retry_or_keyboard"
            else -> "keyboard"
        }
    }
}

