package com.example.voiceinput.transcription.cloud

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.voiceinput.ime.BuildConfig
import com.example.voiceinput.transcription.local.AudioRecordCaptureEngine
import com.example.voiceinput.transcription.local.LocalRecognitionEngine
import com.example.voiceinput.transcription.local.LocalRecognitionListener

class DashScopeRecognitionEngine(
    context: Context,
) : LocalRecognitionEngine {
    private enum class EngineState {
        IDLE,
        CONNECTING,
        RECORDING,
        STOPPING,
        CANCELLING,
    }

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private var listener: LocalRecognitionListener? = null
    private var state: EngineState = EngineState.IDLE
    private var client: DashScopeRealtimeAsrClient? = null
    private var recorder: AudioRecordCaptureEngine? = null
    private var sessionId: Long = 0L
    private var timeoutRunnable: Runnable? = null

    override fun startListening(listener: LocalRecognitionListener) {
        if (state != EngineState.IDLE) {
            listener.onError("Voice engine busy")
            return
        }
        if (BuildConfig.DASHSCOPE_API_KEY.isBlank()) {
            listener.onError("DashScope API key missing")
            return
        }

        this.listener = listener
        sessionId += 1
        val currentSessionId = sessionId
        state = EngineState.CONNECTING
        log("session=$currentSessionId startListening requested")

        val wsClient = DashScopeRealtimeAsrClient(
            apiKey = BuildConfig.DASHSCOPE_API_KEY,
            model = BuildConfig.DASHSCOPE_MODEL,
            listener = object : DashScopeRealtimeAsrClient.Listener {
                override fun onSessionReady() {
                    mainHandler.post {
                        if (state != EngineState.CONNECTING || currentSessionId != sessionId) {
                            return@post
                        }
                        cancelTimeout()
                        startRecorder(currentSessionId)
                    }
                }

                override fun onPartial(text: String) {
                    mainHandler.post {
                        if (currentSessionId == sessionId && state != EngineState.IDLE) {
                            this@DashScopeRecognitionEngine.listener?.onPartial(text)
                        }
                    }
                }

                override fun onCompleted(text: String) {
                    mainHandler.post {
                        if (currentSessionId != sessionId || state == EngineState.IDLE) {
                            return@post
                        }
                        val currentListener = this@DashScopeRecognitionEngine.listener
                        reset("completed")
                        if (text.isBlank()) {
                            currentListener?.onError("No speech recognized")
                        } else {
                            currentListener?.onFinal(text.trim())
                        }
                    }
                }

                override fun onSpeechEnded() {
                    mainHandler.post {
                        if (currentSessionId != sessionId || state != EngineState.RECORDING) {
                            return@post
                        }
                        state = EngineState.STOPPING
                        log("session=$currentSessionId speech ended by server_vad")
                        recorder?.stop()
                        scheduleTimeout(currentSessionId, "awaiting final transcription", FINAL_TIMEOUT_MS) {
                            val currentListener = listener
                            reset("final timeout")
                            currentListener?.onError("DashScope transcription timeout")
                        }
                    }
                }

                override fun onError(message: String) {
                    mainHandler.post {
                        if (currentSessionId != sessionId || state == EngineState.IDLE) {
                            return@post
                        }
                        val currentListener = this@DashScopeRecognitionEngine.listener
                        reset("error")
                        currentListener?.onError(message)
                    }
                }
            },
        )

        client = wsClient
        wsClient.connect()
        scheduleTimeout(currentSessionId, "awaiting DashScope session", CONNECT_TIMEOUT_MS) {
            val currentListener = this.listener
            reset("connect timeout")
            currentListener?.onError("DashScope session timeout")
        }
    }

    override fun stopListening() {
        if (state == EngineState.CONNECTING) {
            cancelListening()
            return
        }
        if (state != EngineState.RECORDING) {
            log("stopListening ignored because state=$state")
            return
        }
        state = EngineState.STOPPING
        log("session=$sessionId stopListening dispatched")
        recorder?.stop()
        client?.finishSession()
        scheduleTimeout(sessionId, "awaiting final transcription", FINAL_TIMEOUT_MS) {
            val currentListener = listener
            reset("final timeout")
            currentListener?.onError("DashScope transcription timeout")
        }
    }

    override fun cancelListening() {
        if (state == EngineState.IDLE) {
            log("cancelListening ignored because state=$state")
            return
        }
        state = EngineState.CANCELLING
        val currentListener = listener
        log("session=$sessionId cancelListening dispatched")
        reset("cancel")
        currentListener?.onCancelled()
    }

    override fun destroy() {
        reset("destroy")
    }

    private fun startRecorder(currentSessionId: Long) {
        val captureEngine = AudioRecordCaptureEngine()
        recorder = captureEngine
        captureEngine.start(
            onStarted = {
                mainHandler.post {
                    if (currentSessionId != sessionId || state != EngineState.CONNECTING) {
                        return@post
                    }
                    state = EngineState.RECORDING
                    log("session=$currentSessionId recorder started")
                    listener?.onReady()
                }
            },
            onChunk = { bytes ->
                if (currentSessionId == sessionId && (state == EngineState.RECORDING || state == EngineState.STOPPING)) {
                    client?.appendAudioChunk(bytes)
                }
            },
            onError = { message ->
                mainHandler.post {
                    if (currentSessionId != sessionId || state == EngineState.IDLE) {
                        return@post
                    }
                    val currentListener = listener
                    reset("record error")
                    currentListener?.onError(message)
                }
            },
        )
    }

    private fun scheduleTimeout(currentSessionId: Long, reason: String, delayMs: Long, action: () -> Unit) {
        cancelTimeout()
        val runnable = Runnable {
            if (currentSessionId != sessionId || state == EngineState.IDLE) {
                return@Runnable
            }
            log("session=$currentSessionId timeout reason=$reason state=$state")
            action()
        }
        timeoutRunnable = runnable
        mainHandler.postDelayed(runnable, delayMs)
    }

    private fun cancelTimeout() {
        timeoutRunnable?.let(mainHandler::removeCallbacks)
        timeoutRunnable = null
    }

    private fun reset(reason: String) {
        log("session=$sessionId reset reason=$reason")
        cancelTimeout()
        try {
            recorder?.destroy()
        } catch (_: Throwable) {
        } finally {
            recorder = null
        }
        try {
            client?.close()
        } catch (_: Throwable) {
        } finally {
            client = null
        }
        listener = null
        state = EngineState.IDLE
    }

    private fun log(message: String) {
        Log.i(LOG_TAG, message)
    }

    companion object {
        private const val LOG_TAG = "DashScopeEngine"
        private const val CONNECT_TIMEOUT_MS = 10_000L
        private const val FINAL_TIMEOUT_MS = 20_000L
    }
}
