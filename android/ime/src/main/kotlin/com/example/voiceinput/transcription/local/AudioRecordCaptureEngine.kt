package com.example.voiceinput.transcription.local

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.util.concurrent.atomic.AtomicBoolean

class AudioRecordCaptureEngine {
    private val isCapturing = AtomicBoolean(false)
    private var audioRecord: AudioRecord? = null
    private var captureThread: Thread? = null

    fun start(
        onStarted: () -> Unit,
        onChunk: (ByteArray) -> Unit,
        onError: (String) -> Unit,
    ) {
        if (isCapturing.get()) {
            onError("Audio capture already running")
            return
        }

        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE_HZ,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        if (minBufferSize <= 0) {
            onError("AudioRecord buffer init failed")
            return
        }

        val bufferSize = minBufferSize.coerceAtLeast(SAMPLE_RATE_HZ)
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE_HZ,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            onError("AudioRecord initialization failed")
            return
        }

        audioRecord = recorder
        isCapturing.set(true)
        captureThread = Thread(
            {
                try {
                    recorder.startRecording()
                    onStarted()
                    val buffer = ByteArray(bufferSize)
                    while (isCapturing.get()) {
                        val bytesRead = recorder.read(buffer, 0, buffer.size)
                        if (bytesRead > 0) {
                            onChunk(buffer.copyOf(bytesRead))
                        } else if (bytesRead < 0 && isCapturing.get()) {
                            onError("AudioRecord read failed: $bytesRead")
                            break
                        }
                    }
                } catch (t: Throwable) {
                    if (isCapturing.get()) {
                        onError("AudioRecord failed: ${t.javaClass.simpleName}")
                    }
                } finally {
                    stopInternal()
                }
            },
            "VoiceInputAudioRecord",
        ).also(Thread::start)
    }

    fun stop() {
        isCapturing.set(false)
        stopRecorder()
    }

    fun cancel() {
        isCapturing.set(false)
        stopRecorder()
    }

    fun destroy() {
        isCapturing.set(false)
        stopInternal()
    }

    private fun stopRecorder() {
        try {
            audioRecord?.stop()
        } catch (_: Throwable) {
        }
    }

    private fun stopInternal() {
        try {
            audioRecord?.release()
        } catch (_: Throwable) {
        } finally {
            audioRecord = null
        }
        captureThread = null
    }

    companion object {
        const val SAMPLE_RATE_HZ: Int = 16_000
    }
}
