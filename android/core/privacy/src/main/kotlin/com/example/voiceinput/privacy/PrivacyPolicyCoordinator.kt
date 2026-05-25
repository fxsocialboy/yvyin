package com.example.voiceinput.privacy

import com.example.voiceinput.model.CloudTransferMode
import com.example.voiceinput.model.PrivacyPolicyRecord

class PrivacyPolicyCoordinator {
    fun requireDisclosure(record: PrivacyPolicyRecord): Boolean {
        return record.cloudTransferMode != CloudTransferMode.NONE
    }

    fun validate(record: PrivacyPolicyRecord) {
        require(!requireDisclosure(record) || record.userDisclosureShown) {
            "Cloud transfer requires explicit disclosure"
        }
    }
}

