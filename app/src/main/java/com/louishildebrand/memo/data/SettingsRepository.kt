package com.louishildebrand.memo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        private val TARGET_LEN = intPreferencesKey("target_len")
        private val ALLOWED_CHARS = stringPreferencesKey("allowed_chars")
    }

    private val targetLen: Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[TARGET_LEN] ?: 8
        }

    suspend fun updateTargetLen(newLen: Int) {
        if (newLen < 1) {
            throw IllegalArgumentException(
                "Target length must be at least 1."
                    + " Got length $newLen."
            )
        }
        context.dataStore.edit { settings ->
            settings[TARGET_LEN] = newLen
        }
    }

    private val allowedChars: Flow<Set<Char>> =
        context.dataStore.data.map { preferences ->
            val str = preferences[ALLOWED_CHARS] ?: "ABDEFGHIJKLMNOPQRSTUVWX"
            str.toCharArray().toSet()
        }

    suspend fun updateAllowedChars(newAllowedChars: Set<Char>) {
        if (newAllowedChars.size < 2) {
            throw IllegalArgumentException(
                "There must be at least two allowed characters."
                    + "Found only ${newAllowedChars.size}."
            )
        }
        val newAllowedCharsStr = newAllowedChars
            .toCharArray()
            .map{ x -> x.uppercaseChar() }
            .sorted()
            .joinToString("")
        context.dataStore.edit { settings ->
            settings[ALLOWED_CHARS] = newAllowedCharsStr
        }
    }

    private val settings: Flow<MemoSettings> =
        this.targetLen.zip(
            this.allowedChars,
            transform = { len, chars -> MemoSettings(chars, len) }
        )

    suspend fun currentSettings(): MemoSettings {
        return this.settings.first()
    }
}