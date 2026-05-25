package com.example.voiceinput.ui

data class CloudDisclosureBannerState(
    val visible: Boolean,
    val message: String,
)

class CloudDisclosureBanner {
    fun showIfNeeded(cloudEnhancementUsed: Boolean): CloudDisclosureBannerState {
        return CloudDisclosureBannerState(
            visible = cloudEnhancementUsed,
            message = if (cloudEnhancementUsed) "Cloud processing is being used for enhanced recognition." else "",
        )
    }
}

