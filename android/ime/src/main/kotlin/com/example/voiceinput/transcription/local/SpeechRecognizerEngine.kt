package com.example.voiceinput.transcription.local

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechRecognizerEngine(
    context: Context,
) : LocalRecognitionEngine {
    private enum class EngineState {
        IDLE,
        STARTING,
        LISTENING,
        STOPPING,
        CANCELLING,
    }

    private val recognizerContext = context
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private var listener: LocalRecognitionListener? = null
    private var state: EngineState = EngineState.IDLE
    private var speechRecognizer: SpeechRecognizer? = null
    private var sessionId: Long = 0L
    private var timeoutRunnable: Runnable? = null
    private var speechStarted = false

    override fun startListening(listener: LocalRecognitionListener) {
        if (state != EngineState.IDLE) {
            log("startListening ignored because state=$state")
            listener.onError("SpeechRecognizer busy")
            return
        }
        if (!SpeechRecognizer.isRecognitionAvailable(appContext)) {
            log("startListening aborted because recognizer unavailable")
            listener.onError("SpeechRecognizer unavailable")
            return
        }

        this.listener = listener
        sessionId += 1
        val currentSessionId = sessionId
        state = EngineState.STARTING
        speechStarted = false
        log("session=$currentSessionId startListening requested")

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.SIMPLIFIED_CHINESE.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, appContext.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        log(
            "session=$currentSessionId intent language=${Locale.SIMPLIFIED_CHINESE.toLanguageTag()} " +
                "partial=true callingPackage=${appContext.packageName}",
        )

        mainHandler.post {
            if (state != EngineState.STARTING || currentSessionId != sessionId) {
                log("session=$currentSessionId start skipped because state=$state currentSession=$sessionId")
                return@post
            }
            try {
                // Recreate the recognizer immediately before startListening so
                // vendor services do not drop the connection while waiting for a later start.
                destroyRecognizer("pre-start reset")
                val recognizer = createRecognizer(listener, currentSessionId) ?: return@post
                mainHandler.postDelayed({
                    if (state != EngineState.STARTING || currentSessionId != sessionId) {
                        log("session=$currentSessionId delayed start skipped because state=$state currentSession=$sessionId")
                        return@postDelayed
                    }
                    state = EngineState.LISTENING
                    log("session=$currentSessionId startListening dispatched")
                    recognizer.startListening(intent)
                    scheduleTimeout(
                        currentSessionId = currentSessionId,
                        reason = "awaiting onReadyForSpeech",
                        delayMs = READY_TIMEOUT_MS,
                        action = TimeoutAction.FAIL,
                    )
                }, START_DISPATCH_DELAY_MS)
            } catch (t: Throwable) {
                log("session=$currentSessionId startListening failed: ${t.javaClass.simpleName}")
                resetEngine("start failure")
                listener.onError("Failed to start speech recognition: ${t.javaClass.simpleName}")
            }
        }
    }

    override fun stopListening() {
        if (state != EngineState.LISTENING) {
            log("stopListening ignored because state=$state")
            return
        }
        state = EngineState.STOPPING
        log("session=$sessionId stopListening dispatched")
        try {
            speechRecognizer?.stopListening()
        } catch (t: Throwable) {
            log("session=$sessionId stopListening failed: ${t.javaClass.simpleName}")
            resetEngine("stop failure")
        }
    }

    override fun cancelListening() {
        if (state != EngineState.STARTING && state != EngineState.LISTENING && state != EngineState.STOPPING) {
            log("cancelListening ignored because state=$state")
            return
        }
        state = EngineState.CANCELLING
        val currentListener = listener
        val currentSessionId = sessionId
        log("session=$currentSessionId cancelListening dispatched")
        try {
            speechRecognizer?.cancel()
        } catch (t: Throwable) {
            log("session=$currentSessionId cancelListening failed: ${t.javaClass.simpleName}")
        }
        resetEngine("cancel")
        currentListener?.onCancelled()
    }

    override fun destroy() {
        log("destroy requested")
        resetEngine("destroy")
    }

    private fun createRecognizer(
        listener: LocalRecognitionListener,
        currentSessionId: Long,
    ): SpeechRecognizer? {
        return try {
            createPlatformRecognizer(currentSessionId).also { recognizer ->
                log("session=$currentSessionId recognizer created")
                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        log("session=$currentSessionId onReadyForSpeech")
                        if (isActiveSession(currentSessionId)) {
                            scheduleTimeout(
                                currentSessionId = currentSessionId,
                                reason = "no speech detected after ready",
                                delayMs = NO_SPEECH_TIMEOUT_MS,
                                action = TimeoutAction.FAIL,
                            )
                            this@SpeechRecognizerEngine.listener?.onReady()
                        }
                    }

                    override fun onBeginningOfSpeech() {
                        speechStarted = true
                        log("session=$currentSessionId onBeginningOfSpeech")
                        scheduleTimeout(
                            currentSessionId = currentSessionId,
                            reason = "speech window exceeded",
                            delayMs = ACTIVE_SPEECH_TIMEOUT_MS,
                            action = TimeoutAction.STOP,
                        )
                    }

                    override fun onRmsChanged(rmsdB: Float) = Unit

                    override fun onBufferReceived(buffer: ByteArray?) = Unit

                    override fun onEndOfSpeech() {
                        log("session=$currentSessionId onEndOfSpeech")
                        scheduleTimeout(
                            currentSessionId = currentSessionId,
                            reason = "awaiting final result after end of speech",
                            delayMs = FINAL_RESULT_TIMEOUT_MS,
                            action = TimeoutAction.FAIL,
                        )
                    }

                    override fun onError(error: Int) {
                        log("session=$currentSessionId onError=$error state=$state")
                        val currentListener = this@SpeechRecognizerEngine.listener ?: return
                        if (state == EngineState.CANCELLING && error == SpeechRecognizer.ERROR_CLIENT) {
                            return
                        }
                        if (!isActiveSession(currentSessionId)) {
                            return
                        }
                        resetEngine("error=$error")
                        currentListener.onError("SpeechRecognizer error code: $error")
                    }

                    override fun onResults(results: Bundle?) {
                        log("session=$currentSessionId onResults")
                        if (!isActiveSession(currentSessionId)) {
                            return
                        }
                        val text = results
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                            ?.trim()
                            .orEmpty()
                        val currentListener = this@SpeechRecognizerEngine.listener ?: return
                        resetEngine("results")
                        if (text.isBlank()) {
                            currentListener.onError("No speech recognized")
                        } else {
                            currentListener.onFinal(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        if (!isActiveSession(currentSessionId)) {
                            return
                        }
                        val text = partialResults
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                            ?.trim()
                            .orEmpty()
                        log("session=$currentSessionId onPartialResults=${if (text.isBlank()) "<empty>" else text}")
                        if (text.isNotBlank()) {
                            this@SpeechRecognizerEngine.listener?.onPartial(text)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {
                        log("session=$currentSessionId onEvent type=$eventType")
                    }
                })
                speechRecognizer = recognizer
            }
        } catch (t: Throwable) {
            log("session=$currentSessionId recognizer create failed: ${t.javaClass.simpleName}")
            resetEngine("create failure")
            listener.onError("Failed to initialize speech recognition: ${t.javaClass.simpleName}")
            null
        }
    }

    private fun createPlatformRecognizer(currentSessionId: Long): SpeechRecognizer {
        val component = resolveRecognitionServiceComponent()
        return if (component != null) {
            log("session=$currentSessionId using recognitionService=${component.flattenToShortString()}")
            SpeechRecognizer.createSpeechRecognizer(recognizerContext, component)
        } else {
            log("session=$currentSessionId using default recognitionService")
            SpeechRecognizer.createSpeechRecognizer(recognizerContext)
        }
    }

    private fun resolveRecognitionServiceComponent(): ComponentName? {
        val flattened = try {
            Settings.Secure.getString(appContext.contentResolver, "voice_recognition_service")
        } catch (_: Throwable) {
            null
        }?.trim().orEmpty()
        if (flattened.isBlank()) {
            return null
        }
        return ComponentName.unflattenFromString(flattened)
    }

    private fun isActiveSession(currentSessionId: Long): Boolean {
        return currentSessionId == sessionId && state != EngineState.IDLE
    }

    private fun scheduleTimeout(
        currentSessionId: Long,
        reason: String,
        delayMs: Long,
        action: TimeoutAction,
    ) {
        cancelTimeout()
        val runnable = Runnable {
            if (!isActiveSession(currentSessionId)) {
                return@Runnable
            }
            log("session=$currentSessionId timeout fired reason=$reason state=$state")
            when (action) {
                TimeoutAction.STOP -> {
                    if (state == EngineState.LISTENING) {
                        stopListening()
                    }
                    scheduleTimeout(
                        currentSessionId = currentSessionId,
                        reason = "awaiting final result after forced stop",
                        delayMs = FINAL_RESULT_TIMEOUT_MS,
                        action = TimeoutAction.FAIL,
                    )
                }
                TimeoutAction.FAIL -> {
                    val currentListener = listener
                    resetEngine("timeout: $reason")
                    currentListener?.onError("Speech recognition timeout: $reason")
                }
            }
        }
        timeoutRunnable = runnable
        mainHandler.postDelayed(runnable, delayMs)
    }

    private fun cancelTimeout() {
        timeoutRunnable?.let(mainHandler::removeCallbacks)
        timeoutRunnable = null
    }

    private fun resetEngine(reason: String) {
        log("session=$sessionId resetEngine reason=$reason")
        cancelTimeout()
        state = EngineState.IDLE
        speechStarted = false
        listener = null
        destroyRecognizer(reason)
    }

    private fun destroyRecognizer(reason: String) {
        val recognizer = speechRecognizer ?: return
        try {
            log("session=$sessionId destroyRecognizer reason=$reason")
            recognizer.destroy()
        } catch (t: Throwable) {
            log("session=$sessionId destroyRecognizer failed: ${t.javaClass.simpleName}")
        } finally {
            speechRecognizer = null
        }
    }

    private fun log(message: String) {
        Log.i(LOG_TAG, message)
    }

    companion object {
        private const val LOG_TAG = "VoiceInputRecognizer"
        private const val START_DISPATCH_DELAY_MS = 300L
        private const val READY_TIMEOUT_MS = 8_000L
        private const val NO_SPEECH_TIMEOUT_MS = 6_000L
        private const val ACTIVE_SPEECH_TIMEOUT_MS = 10_000L
        private const val FINAL_RESULT_TIMEOUT_MS = 3_000L
    }

    private enum class TimeoutAction {
        STOP,
        FAIL,
    }
}
