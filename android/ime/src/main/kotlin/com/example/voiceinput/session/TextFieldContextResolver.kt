package com.example.voiceinput.session

import com.example.voiceinput.model.EntryContext
import com.example.voiceinput.model.FieldType
import com.example.voiceinput.model.TextFieldContext

class TextFieldContextResolver {
    fun resolve(sessionId: String, entryContext: EntryContext): TextFieldContext {
        val fieldType = when (entryContext) {
            EntryContext.CHAT -> FieldType.CHAT_BOX
            EntryContext.SEARCH -> FieldType.SEARCH_BOX
            EntryContext.FORM -> FieldType.SINGLE_LINE_FORM
            EntryContext.MEMO -> FieldType.MEMO_BOX
        }
        return TextFieldContext("ctx-$sessionId", sessionId, fieldType, 0, 0..0)
    }
}

