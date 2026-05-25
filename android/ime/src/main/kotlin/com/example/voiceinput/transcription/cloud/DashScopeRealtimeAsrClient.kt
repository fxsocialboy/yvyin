package com.example.voiceinput.transcription.cloud

import android.util.Log
import com.example.voiceinput.transcription.local.AudioRecordCaptureEngine
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.Base64
import java.util.concurrent.TimeUnit

class DashScopeRealtimeAsrClient(
    private val apiKey: String,
    private val model: String,
    private val listener: Listener,
) {
    interface Listener {
        fun onSessionReady()
        fun onPartial(text: String)
        fun onCompleted(text: String)
        fun onError(message: String)
    }

    private val httpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url("$WS_URL?model=$model")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("OpenAI-Beta", "realtime=v1")
            .build()
        webSocket = httpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                log("websocket opened")
                val sessionUpdate = JSONObject()
                    .put("type", "session.update")
                    .put(
                        "session",
                        JSONObject()
                            .put("modalities", org.json.JSONArray().put("text"))
                            .put("input_audio_format", "pcm")
                            .put("sample_rate", AudioRecordCaptureEngine.SAMPLE_RATE_HZ)
                            .put("turn_detection", JSONObject.NULL)
                            .put(
                                "input_audio_transcription",
                                JSONObject()
                                    .put("language", "zh"),
                            ),
                    )
                    .toString()
                log("session.update=$sessionUpdate")
                webSocket.send(sessionUpdate)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                log("websocket failure=${t.javaClass.simpleName}")
                listener.onError("DashScope connection failed: ${t.javaClass.simpleName}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                log("websocket closing code=$code reason=$reason")
            }
        })
    }

    fun appendAudioChunk(audioChunk: ByteArray) {
        log("appendAudioChunk bytes=${audioChunk.size}")
        val payload = JSONObject()
            .put("type", "input_audio_buffer.append")
            .put("audio", Base64.getEncoder().encodeToString(audioChunk))
            .toString()
        webSocket?.send(payload)
    }

    fun commitAudio() {
        webSocket?.send(JSONObject().put("type", "input_audio_buffer.commit").toString())
    }

    fun close() {
        try {
            webSocket?.close(1000, "done")
        } catch (_: Throwable) {
        } finally {
            webSocket = null
            httpClient.dispatcher.executorService.shutdown()
        }
    }

    private fun handleMessage(text: String) {
        val root = JSONObject(text)
        when (val type = root.optString("type")) {
            "session.created",
            "session.updated" -> {
                log("event=$type")
                listener.onSessionReady()
            }

            "conversation.item.input_audio_transcription.delta" -> {
                val delta = root.optString("delta")
                    .ifBlank { root.optJSONObject("item")?.optString("delta").orEmpty() }
                if (delta.isNotBlank()) {
                    log("partial=$delta")
                    listener.onPartial(delta)
                }
            }

            "conversation.item.input_audio_transcription.completed" -> {
                val transcript = root.optString("transcript")
                    .ifBlank { root.optJSONObject("item")?.optString("transcript").orEmpty() }
                log("completed=$transcript")
                listener.onCompleted(transcript)
            }

            "error" -> {
                val errorObject = root.optJSONObject("error")
                val message = errorObject?.optString("message").orEmpty().ifBlank { text }
                log("errorRaw=$text")
                log("error=$message")
                listener.onError("DashScope error: $message")
            }

            else -> log("ignored event=$type")
        }
    }

    private fun log(message: String) {
        Log.i(LOG_TAG, message)
    }

    companion object {
        private const val LOG_TAG = "DashScopeAsrClient"
        private const val WS_URL = "wss://dashscope.aliyuncs.com/api-ws/v1/realtime"
    }
}
