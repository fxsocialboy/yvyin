package com.example.voiceinput.data.local.settings

import com.example.voiceinput.model.PunctuationMode

data class VoiceInputSettings(
    val punctuationMode: PunctuationMode = PunctuationMode.AUTO,
    val cloudEnhancementEnabled: Boolean = true,
)

class VoiceInputSettingsStore {
    private var settings = VoiceInputSettings()

    fun load(): VoiceInputSettings = settings

    fun update(transform: (VoiceInputSettings) -> VoiceInputSettings): VoiceInputSettings {
        settings = transform(settings)
        return settings
    }
}

