package com.example.voiceinput.session

class FallbackTransitionCoordinator {
    fun nextMode(networkAvailable: Boolean, appFocused: Boolean): String {
        return when {
            !appFocused -> "keyboard"
            !networkAvailable -> "local_only"
            else -> "continue_voice"
        }
    }
}

