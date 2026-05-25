package contract

import com.example.voiceinput.data.network.cloud.CloudEnhancementClient
import com.example.voiceinput.data.network.cloud.RecognitionEnhancementRequest
import com.example.voiceinput.model.NetworkMode
import com.example.voiceinput.model.PunctuationMode
import kotlin.test.Test
import kotlin.test.assertEquals

class CloudRecognitionInterfaceTest {
    @Test
    fun returnsMatchingSessionAndSegmentIds() {
        val response = CloudEnhancementClient().enhance(
            RecognitionEnhancementRequest("s1", "seg1", contextType = "chat", baselinePartialText = "ni hao", punctuationMode = PunctuationMode.AUTO, networkMode = NetworkMode.STRONG)
        )
        assertEquals("s1", response.sessionId)
        assertEquals("seg1", response.segmentId)
    }
}

