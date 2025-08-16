package com.louishildebrand.memo.ui

import kotlin.time.Duration
import kotlin.time.TimeSource

sealed interface AppState {
    data object Start : AppState
    data class Memo(
        val target: String,
        val idx: Int,
        val start: TimeSource.Monotonic.ValueTimeMark
    ) : AppState
    data class Check(
        val target: String,
        val partialGuess: String,
        val memoDuration: Duration,
        val recallStart: TimeSource.Monotonic.ValueTimeMark
    ) : AppState
    data class Success(
        val target: String,
        val memoDuration: Duration,
        val recallDuration: Duration
    ) : AppState
    data class Failure(
        val target: String,
        val guess: String,
        val memoDuration: Duration,
        val recallDuration: Duration
    ) : AppState
}