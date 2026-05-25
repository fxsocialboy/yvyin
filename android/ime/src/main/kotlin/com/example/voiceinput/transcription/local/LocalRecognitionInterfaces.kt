package com.example.voiceinput.transcription.local

interface LocalRecognitionListener {
    fun onReady()
    fun onPartial(text: String)
    fun onFinal(text: String)
    fun onError(message: String)
    fun onCancelled()
}

interface LocalRecognitionEngine {
    fun startListening(listener: LocalRecognitionListener)
    fun stopListening()
    fun cancelListening()
    fun destroy()
}
