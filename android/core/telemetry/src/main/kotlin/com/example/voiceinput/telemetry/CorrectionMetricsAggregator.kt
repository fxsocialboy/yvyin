package com.example.voiceinput.telemetry

data class CorrectionMetricsSummary(
    val correctionCount: Int,
    val acceptableRecognitionRate: Double,
)

class CorrectionMetricsAggregator {
    fun summarize(corrections: Int, totalSegments: Int): CorrectionMetricsSummary {
        val accepted = (totalSegments - corrections).coerceAtLeast(0)
        val rate = if (totalSegments == 0) 0.0 else accepted.toDouble() / totalSegments.toDouble()
        return CorrectionMetricsSummary(corrections, rate)
    }
}

