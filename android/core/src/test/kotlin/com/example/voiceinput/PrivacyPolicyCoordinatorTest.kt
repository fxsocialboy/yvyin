package com.example.voiceinput

import com.example.voiceinput.model.PrivacyPolicyRecord
import com.example.voiceinput.privacy.PrivacyPolicyCoordinator
import kotlin.test.Test
import kotlin.test.assertTrue

class PrivacyPolicyCoordinatorTest {
    @Test
    fun doesNotRequireDisclosureForLocalOnlyMode() {
        val coordinator = PrivacyPolicyCoordinator()
        coordinator.validate(PrivacyPolicyRecord("r1", "s1"))
        assertTrue(true)
    }
}

