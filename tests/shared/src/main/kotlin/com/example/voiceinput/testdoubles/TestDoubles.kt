package com.example.voiceinput.testdoubles

import com.example.voiceinput.data.network.cloud.CloudEnhancementClient
import com.example.voiceinput.data.network.cloud.RecognitionEnhancementRequest
import com.example.voiceinput.data.network.cloud.RecognitionEnhancementResponse
import com.example.voiceinput.transcription.local.LocalRecognitionEngine
import com.example.voiceinput.transcription.local.LocalRecognitionResult

class FakeRecognizer : LocalRecognitionEngine {
    override fun start() = Unit
    override fun stop() = Unit
    override fun consumeAudioChunk(chunk: ByteArray): LocalRecognitionResult {
        return LocalRecognitionResult(partialText = "ni", finalText = "ni hao", confidence = 0.9f)
    }
}

class FakeGatewayClient : CloudEnhancementClient() {
    override fun enhance(request: RecognitionEnhancementRequest): RecognitionEnhancementResponse {
        return RecognitionEnhancementResponse(request.sessionId, request.segmentId, "ni hao")
    }
}

data class SessionFixture(val id: String = "session-1", val segmentId: String = "segment-1")

