package com.louishildebrand.memo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.louishildebrand.memo.MemoApplication
import com.louishildebrand.memo.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repo: SettingsRepository
) : ViewModel() {
    private var _state = MutableStateFlow(SettingsUiState.empty())
    val state = this._state.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = repo.currentSettings()
            _state.value = SettingsUiState(
                allowedCharsString = settings.allowedChars
                    .toList()
                    .sorted()
                    .joinToString(""),
                targetLenString = settings.len.toString(),
            )
        }
    }

    fun updateAllowedChars(newAllowedChars: String) {
        this._state.value = this.state.value.copy(
            allowedCharsString = newAllowedChars.uppercase()
        )
        val newSet = newAllowedChars.toCharArray().map { x -> x.uppercaseChar() }.toSet()
        // TODO: Alert user if setting is invalid
        if (newSet.size < 2) return
        viewModelScope.launch {
            repo.updateAllowedChars(newSet)
        }
    }

    fun updateTargetLen(newLen: String) {
        this._state.value = this.state.value.copy(targetLenString = newLen)
        val n = try {
            newLen.toInt()
        } catch (exc: NumberFormatException) {
            return
        }
        // TODO: Alert user if setting is invalid
        if (n <= 2) {
            return
        }
        viewModelScope.launch {
            repo.updateTargetLen(n)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (this[APPLICATION_KEY] as MemoApplication).settingsRepository
                SettingsViewModel(repo)
            }
        }
    }
}
