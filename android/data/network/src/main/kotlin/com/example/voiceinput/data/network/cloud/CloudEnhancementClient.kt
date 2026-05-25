package com.example.voiceinput.data.network.cloud

import com.example.voiceinput.model.NetworkMode
import com.example.voiceinput.model.PunctuationMode

data class RecognitionEnhancementRequest(
    val sessionId: String,
    val segmentId: String,
    val locale: String = "zh-CN",
    val contextType: String,
    val baselinePartialText: String,
    val punctuationMode: PunctuationMode,
    val networkMode: NetworkMode,
)

data class RecognitionEnhancementResponse(
    val sessionId: String,
    val segmentId: String,
    val enhancedText: String,
    val replacementMode: String = "replace_segment",
    val policyFlags: List<String> = listOf("no_store", "user_disclosed"),
)

class CloudEnhancementPolicyGate {
    fun allow(networkMode: NetworkMode, cloudEnabled: Boolean): Boolean {
        return cloudEnabled && networkMode in setOf(NetworkMode.NORMAL, NetworkMode.STRONG)
    }
}

open class CloudEnhancementClient {
    open fun enhance(request: RecognitionEnhancementRequest): RecognitionEnhancementResponse {
        return RecognitionEnhancementResponse(
            sessionId = request.sessionId,
            segmentId = request.segmentId,
            enhancedText = request.baselinePartialText.ifBlank { "enhanced" },
        )
    }
}
