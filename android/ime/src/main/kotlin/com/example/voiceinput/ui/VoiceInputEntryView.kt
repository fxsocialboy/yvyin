package com.example.voiceinput.ui

data class VoiceInputEntryViewState(
    val isListening: Boolean = false,
    val canCancel: Boolean = true,
)

class VoiceInputEntryView {
    fun startListening(): VoiceInputEntryViewState = VoiceInputEntryViewState(isListening = true)
    fun stopListening(): VoiceInputEntryViewState = VoiceInputEntryViewState(isListening = false)
}

