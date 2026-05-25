package com.example.voiceinput.ime

import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.voiceinput.capture.VoiceCaptureCoordinator
import com.example.voiceinput.model.EntryContext
import com.example.voiceinput.model.InterruptionReason
import com.example.voiceinput.model.SessionState
import com.example.voiceinput.model.VoiceInputSession
import com.example.voiceinput.session.VoiceInputSessionManager
import com.example.voiceinput.transcription.cloud.DashScopeRecognitionEngine
import com.example.voiceinput.transcription.local.LocalRecognitionListener

class VoiceInputImeService : InputMethodService(), LocalRecognitionListener {
    private val sessionManager = VoiceInputSessionManager()
    private val captureCoordinator = VoiceCaptureCoordinator(sessionManager)

    private var activeSession: VoiceInputSession? = null
    private lateinit var triggerButton: Button
    private lateinit var statusView: TextView
    private var recognizerEngine: DashScopeRecognitionEngine? = null
    private var voiceStartPosted = false
    private var isReadyForAudio = false
    private var awaitingFinalResult = false

    override fun onCreateInputView(): View {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        statusView = TextView(this).apply {
            text = getString(com.example.voiceinput.ime.R.string.ime_status_idle)
        }

        triggerButton = Button(this).apply {
            text = getString(com.example.voiceinput.ime.R.string.ime_button_start)
            setOnClickListener { toggleVoiceInput() }
        }

        container.addView(
            statusView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )
        container.addView(
            triggerButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )

        return container
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        resetUiState()
    }

    override fun onFinishInput() {
        super.onFinishInput()
        log("onFinishInput")
        recognizerEngine?.cancelListening()
        resetUiState()
    }

    override fun onDestroy() {
        log("onDestroy")
        recognizerEngine?.destroy()
        super.onDestroy()
    }

    private fun toggleVoiceInput() {
        log("toggleVoiceInput state=${activeSession?.sessionState} posted=$voiceStartPosted")
        val current = activeSession
        if (current != null && current.sessionState == SessionState.LISTENING) {
            when {
                awaitingFinalResult || !isReadyForAudio -> {
                    recognizerEngine?.cancelListening()
                    activeSession = captureCoordinator.cancel(current)
                    updateStatus(getString(com.example.voiceinput.ime.R.string.ime_status_cancelled))
                    updateTriggerButton(getString(com.example.voiceinput.ime.R.string.ime_button_start))
                }

                else -> {
                    awaitingFinalResult = true
                    isReadyForAudio = false
                    recognizerEngine?.stopListening()
                    updateStatus(getString(com.example.voiceinput.ime.R.string.ime_status_processing))
                    updateTriggerButton(getString(com.example.voiceinput.ime.R.string.ime_button_cancel))
                }
            }
            return
        }
        if (voiceStartPosted) {
            log("toggleVoiceInput ignored because start already posted")
            return
        }

        val inputConnection = currentInputConnection
        if (inputConnection == null) {
            updateStatus(getString(com.example.voiceinput.ime.R.string.ime_status_missing_connection))
            return
        }

        inputConnection.finishComposingText()
        activeSession = captureCoordinator.begin(
            sessionId = "session-${System.currentTimeMillis()}",
            entryContext = resolveEntryContext(currentInputEditorInfo),
        )
        isReadyForAudio = false
        awaitingFinalResult = false
        updateStatus(getString(com.example.voiceinput.ime.R.string.ime_status_connecting))
        updateTriggerButton(getString(com.example.voiceinput.ime.R.string.ime_button_cancel))
        voiceStartPosted = true
        triggerButton.post {
            voiceStartPosted = false
            if (activeSession?.sessionState != SessionState.LISTENING) {
                log("posted start aborted because session state=${activeSession?.sessionState}")
                return@post
            }
            log("posted startListening")
            getOrCreateRecognizerEngine().startListening(this)
        }
    }

    private fun resetUiState() {
        voiceStartPosted = false
        isReadyForAudio = false
        awaitingFinalResult = false
        activeSession = null
        if (::statusView.isInitialized) {
            statusView.text = getString(com.example.voiceinput.ime.R.string.ime_status_idle)
        }
        if (::triggerButton.isInitialized) {
            triggerButton.text = getString(com.example.voiceinput.ime.R.string.ime_button_start)
        }
    }

    private fun resolveEntryContext(editorInfo: EditorInfo?): EntryContext {
        val inputTypeClass = editorInfo?.inputType?.and(InputType.TYPE_MASK_CLASS)
        return when (inputTypeClass) {
            InputType.TYPE_CLASS_TEXT -> EntryContext.CHAT
            InputType.TYPE_CLASS_NUMBER -> EntryContext.FORM
            else -> EntryContext.MEMO
        }
    }

    override fun onReady() {
        log("onReady")
        isReadyForAudio = true
        awaitingFinalResult = false
        updateStatus(getString(com.example.voiceinput.ime.R.string.ime_status_listening))
        updateTriggerButton(getString(com.example.voiceinput.ime.R.string.ime_button_stop))
    }

    override fun onPartial(text: String) {
        log("onPartial text=$text")
        isReadyForAudio = true
        updateStatus(text)
    }

    override fun onFinal(text: String) {
        log("onFinal text=$text")
        isReadyForAudio = false
        awaitingFinalResult = false
        val inputConnection = currentInputConnection
        val session = activeSession
        if (inputConnection == null || session == null) {
            updateStatus(getString(com.example.voiceinput.ime.R.string.ime_status_missing_connection))
            updateTriggerButton(getString(com.example.voiceinput.ime.R.string.ime_button_start))
            return
        }

        inputConnection.commitText(text, 1)
        activeSession = captureCoordinator.complete(session, text.length)
        updateStatus(getString(com.example.voiceinput.ime.R.string.ime_status_result))
        updateTriggerButton(getString(com.example.voiceinput.ime.R.string.ime_button_start))
    }

    override fun onError(message: String) {
        log("onError message=$message")
        isReadyForAudio = false
        awaitingFinalResult = false
        val session = activeSession
        if (session != null) {
            activeSession = captureCoordinator.fail(session, InterruptionReason.RECOGNITION_ERROR)
        }
        updateStatus("${getString(com.example.voiceinput.ime.R.string.ime_status_error)} ($message)")
        updateTriggerButton(getString(com.example.voiceinput.ime.R.string.ime_button_start))
    }

    override fun onCancelled() {
        log("onCancelled")
        isReadyForAudio = false
        awaitingFinalResult = false
        val session = activeSession
        if (session != null && session.sessionState == SessionState.LISTENING) {
            activeSession = captureCoordinator.cancel(session)
        }
        updateStatus(getString(com.example.voiceinput.ime.R.string.ime_status_cancelled))
        updateTriggerButton(getString(com.example.voiceinput.ime.R.string.ime_button_start))
    }

    private fun getOrCreateRecognizerEngine(): DashScopeRecognitionEngine {
        return recognizerEngine ?: DashScopeRecognitionEngine(this).also { recognizerEngine = it }
    }

    private fun updateStatus(text: String) {
        if (::statusView.isInitialized) {
            statusView.text = text
        }
    }

    private fun updateTriggerButton(text: String) {
        if (::triggerButton.isInitialized) {
            triggerButton.text = text
        }
    }

    private fun log(message: String) {
        Log.d(LOG_TAG, message)
    }

    companion object {
        private const val LOG_TAG = "VoiceInputIme"
    }
}
