package com.example.voiceinput.data.network

import com.example.voiceinput.model.NetworkMode

class NetworkQualityMonitor {
    fun evaluate(signalStrength: Int): NetworkMode {
        return when {
            signalStrength <= 0 -> NetworkMode.OFFLINE
            signalStrength < 30 -> NetworkMode.WEAK
            signalStrength < 70 -> NetworkMode.NORMAL
            else -> NetworkMode.STRONG
        }
    }
}

