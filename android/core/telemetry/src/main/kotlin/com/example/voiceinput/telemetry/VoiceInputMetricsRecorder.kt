package com.example.voiceinput.telemetry

data class VoiceInputMetrics(
    val taskCompletionMs: Long = 0,
    val correctionCount: Int = 0,
    val undoCount: Int = 0,
    val fallbackCount: Int = 0,
    val firstFeedbackMs: Long = 0,
)

class VoiceInputMetricsRecorder {
    private val metrics = mutableListOf<VoiceInputMetrics>()
    fun record(metric: VoiceInputMetrics) {
        metrics += metric
    }
    fun all(): List<VoiceInputMetrics> = metrics.toList()
}

