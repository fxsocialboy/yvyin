package com.example.voiceinput.session

class CursorContinuationAppender {
    fun appendAtCursor(text: String, cursor: Int, newText: String): String {
        return text.substring(0, cursor) + newText + text.substring(cursor)
    }
}

