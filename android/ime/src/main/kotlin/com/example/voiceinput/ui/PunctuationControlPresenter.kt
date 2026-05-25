package com.example.voiceinput.ui

import com.example.voiceinput.model.PunctuationMode

class PunctuationControlPresenter {
    fun toggle(current: PunctuationMode): PunctuationMode {
        return if (current == PunctuationMode.AUTO) PunctuationMode.OFF else PunctuationMode.AUTO
    }
}

