package com.louishildebrand.memo.ui

data class SettingsUiState(
    val allowedCharsString: String,
    val targetLenString: String,
) {
    companion object {
        fun empty(): SettingsUiState {
            return SettingsUiState(allowedCharsString = "", targetLenString = "")
        }
    }
}
