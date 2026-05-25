package com.example.voiceinput.session

import com.example.voiceinput.model.ActionTarget
import com.example.voiceinput.model.CorrectionAction
import com.example.voiceinput.model.CorrectionActionType
import com.example.voiceinput.model.PostActionResult

class CorrectionActionTracker {
    fun track(sessionId: String, segmentId: String?, actionType: CorrectionActionType): CorrectionAction {
        return CorrectionAction(
            correctionId = "correction-$sessionId-${segmentId ?: "session"}",
            sessionId = sessionId,
            segmentId = segmentId,
            actionType = actionType,
            actionTarget = ActionTarget.CHARACTER_RANGE,
            postActionResult = PostActionResult.ACCEPTED_TEXT,
        )
    }
}

