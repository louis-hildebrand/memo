package com.louishildebrand.memo.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsState(
    val allowedCharsString: String,
    val targetLenString: String,
) {
    companion object {
        fun fromSettings(settings: MemoSettings): SettingsState {
            return SettingsState(
                allowedCharsString = settings.allowedChars
                    .toList()
                    .map{ x -> x.uppercaseChar() }
                    .sorted()
                    .joinToString(""),
                targetLenString = settings.len.toString()
            )
        }
    }
}

class SettingsViewModel(settings: MemoSettings? = null) : ViewModel() {
    var settings: MemoSettings = settings ?: MemoSettings()

    private var _state = MutableStateFlow(SettingsState.fromSettings(this.settings))
    val state = this._state.asStateFlow()

    fun updateSettings(settings: MemoSettings) {
        this.settings = settings
        this._state.value = SettingsState.fromSettings(this.settings)
    }

    fun updateAllowedChars(newAllowedChars: String) {
        this._state.value = this.state.value.copy(
            allowedCharsString = newAllowedChars.uppercase()
        )
        val newSet = newAllowedChars.toCharArray().map { x -> x.uppercaseChar() }.toSet()
        if (newSet.size < 2) return
        this.settings = this.settings.copy(
            allowedChars = newSet
        )
    }

    fun updateTargetLen(newLen: String) {
        this._state.value = this.state.value.copy(targetLenString = newLen)
        val n = try {
            newLen.toInt()
        } catch (exc: NumberFormatException) {
            return
        }
        if (n <= 2) {
            return
        }
        this.settings = this.settings.copy(len = n)
    }
}
