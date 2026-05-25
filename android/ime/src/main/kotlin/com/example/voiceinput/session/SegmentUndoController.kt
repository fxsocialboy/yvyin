package com.example.voiceinput.session

class SegmentUndoController {
    fun undo(existingText: String, latestSegmentText: String): String {
        return existingText.removeSuffix(latestSegmentText)
    }
}

