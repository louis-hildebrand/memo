package com.louishildebrand.memo.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.toDuration

class MemoViewModel : ViewModel() {
    private val _state = MutableStateFlow<AppState>(AppState.Start)
    val state: StateFlow<AppState> = _state.asStateFlow()

    // TODO: Allow updating it
    private val settings = MemoSettings()

    // TODO: Validate transitions

    fun start() {
        when (this.state.value) {
            is AppState.Start,
            is AppState.Success,
            is AppState.Failure -> {
                this._state.value = AppState.Memo(
                    target = this.makeTarget(),
                    idx = 0,
                    start = TimeSource.Monotonic.markNow(),
                )
            }
            else -> {
                throw IllegalStateException(
                    "Cannot start memorization from state ${this.state.value}.")
            }
        }
    }

    fun nextMemoLetter() {
        when (this.state.value) {
            is AppState.Memo -> {
                val s = this.state.value as AppState.Memo
                val isDone = s.idx + 1 >= s.target.length
                this._state.value = if (isDone) {
                    val now = TimeSource.Monotonic.markNow()
                    val duration = truncateDuration(now - s.start)
                    AppState.Check(
                        target = s.target,
                        partialGuess = "",
                        memoDuration = duration,
                        recallStart = now
                    )
                } else {
                    AppState.Memo(target = s.target, idx = s.idx + 1, start = s.start)
                }
            }
            else -> {
                throw IllegalStateException(
                    "Cannot move to the next memo letter when in state ${this.state.value}.")
            }
        }
    }

    fun updateGuess(newGuess: String) {
        when (this.state.value) {
            is AppState.Check -> {
                val s = this.state.value as AppState.Check
                this._state.value = AppState.Check(
                    target = s.target,
                    partialGuess = newGuess.uppercase(),
                    memoDuration = s.memoDuration,
                    recallStart = s.recallStart
                )
            }
            else -> {
                throw IllegalStateException(
                    "Cannot update guess when in state ${this.state.value}.")
            }
        }
    }

    fun submitGuess() {
        when (this.state.value) {
            is AppState.Check -> {
                val s = this.state.value as AppState.Check
                val now = TimeSource.Monotonic.markNow()
                val recallDuration = truncateDuration(now - s.recallStart)
                val isCorrect = s.partialGuess.uppercase() == s.target.uppercase()
                this._state.value = if (isCorrect) {
                    AppState.Success(
                        target = s.target,
                        memoDuration = s.memoDuration,
                        recallDuration = recallDuration
                    )
                } else {
                    AppState.Failure(
                        target = s.target,
                        guess = s.partialGuess,
                        memoDuration = s.memoDuration,
                        recallDuration = recallDuration
                    )
                }
            }
            else -> {
                throw IllegalStateException(
                    "Cannot submit guess when in state ${this.state.value}.")
            }
        }
    }

    private fun makeTarget(): String {
        if (this.settings.allowedChars.size < 2) {
            throw IllegalArgumentException(
                "List of allowed characters must have at least two elements,"
                        + " but found only ${this.settings.allowedChars.size}.")
        }
        var target = ""
        for (i in 1..this.settings.len) {
            val prev: Char? = if (target.isEmpty()) {
                null
            } else {
                target.last()
            }
            var next: Char
            do {
                next = this.settings.allowedChars.random()
            } while (next == prev)
            target += next
        }
        return target
    }

    private fun truncateDuration(duration: Duration): Duration {
        val millis = duration.toLong(DurationUnit.MILLISECONDS)
        val truncated = (millis / 10L) * 10L
        return truncated.toDuration(DurationUnit.MILLISECONDS)
    }
}
