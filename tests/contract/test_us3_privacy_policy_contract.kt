package contract

import com.example.voiceinput.model.CloudTransferMode
import com.example.voiceinput.model.PrivacyPolicyRecord
import com.example.voiceinput.privacy.PrivacyPolicyCoordinator
import kotlin.test.Test
import kotlin.test.assertFailsWith

class Us3PrivacyPolicyContractTest {
    @Test
    fun disclosureIsRequiredForCloudTransfer() {
        val coordinator = PrivacyPolicyCoordinator()
        assertFailsWith<IllegalArgumentException> {
            coordinator.validate(
                PrivacyPolicyRecord(
                    recordId = "1",
                    sessionId = "s1",
                    cloudTransferMode = CloudTransferMode.PARTIAL_AUDIO,
                    userDisclosureShown = false,
                )
            )
        }
    }
}

