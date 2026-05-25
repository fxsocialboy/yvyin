package com.example.voiceinput.ui

class ActiveFieldRenderer {
    fun render(existingText: String, partialText: String, finalText: String): String {
        return if (finalText.isNotBlank()) existingText + finalText else existingText + partialText
    }
}

