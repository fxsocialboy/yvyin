package com.example.voiceinput.telemetry

class VoiceInputSessionTelemetryBinder(
    private val recorder: VoiceInputMetricsRecorder,
) {
    fun recordFirstFeedback(firstFeedbackMs: Long) {
        recorder.record(VoiceInputMetrics(firstFeedbackMs = firstFeedbackMs))
    }
}
