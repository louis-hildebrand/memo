package com.louishildebrand.memo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.toDuration

val ALLOWED_CHARS = ('A'..'B') + ('D'..'X')

@Composable
fun MemoApp(allowedChars: List<Char> = ALLOWED_CHARS, len: Int = 8) {
    // TODO: Fix colours to work in both light and dark modes
    // TODO: Be more consistent with fonts (https://developer.android.com/codelabs/jetpack-compose-theming#5)
    // TODO: Let user choose allowed characters, memo length
    // TODO: Save results, show stats
    var state: AppState by remember { mutableStateOf(AppState.Start) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            AppState.Start ->
                Surface(
                    onClick = {
                        state = AppState.Memo(
                            target = makeTarget(allowedChars, len),
                            idx = 0,
                            start = TimeSource.Monotonic.markNow()
                        )
                    }
                ) {
                    StartScreen()
                }

            is AppState.Memo -> {
                val s = state as AppState.Memo
                Surface(
                    onClick = {
                        state = if (s.idx + 1 == s.target.length) {
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
                    },
                ) {
                    MemoScreen(c = s.target[s.idx])
                }
            }

            is AppState.Check -> {
                val s = state as AppState.Check
                // TODO: Surely there's a better way
                CheckScreen(
                    currentGuess = s.partialGuess,
                    update = { guess ->
                        state = AppState.Check(
                            target = s.target,
                            partialGuess = guess.uppercase(),
                            memoDuration = s.memoDuration,
                            recallStart = s.recallStart
                        )
                    },
                    submit = {
                        val now = TimeSource.Monotonic.markNow()
                        val recallDuration = truncateDuration(now - s.recallStart)
                        state = if (s.partialGuess.uppercase() == s.target.uppercase()) {
                            AppState.Success(
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
                )
            }

            is AppState.Success -> {
                val s = state as AppState.Success
                Surface(
                    onClick = {
                        state = AppState.Memo(
                            target = makeTarget(allowedChars, len),
                            idx = 0,
                            start = TimeSource.Monotonic.markNow()
                        )
                    }
                ) {
                    SuccessScreen(
                        memoDuration = s.memoDuration,
                        recallDuration = s.recallDuration
                    )
                }
            }

            is AppState.Failure -> {
                val s = state as AppState.Failure
                Surface(
                    onClick = {
                        state = AppState.Memo(
                            target = makeTarget(allowedChars, len),
                            idx = 0,
                            start = TimeSource.Monotonic.markNow()
                        )
                    }
                ) {
                    FailureScreen(
                        target = s.target,
                        guess = s.guess,
                        memoDuration = s.memoDuration,
                        recallDuration = s.recallDuration
                    )
                }
            }
        }
    }
}

fun truncateDuration(duration: Duration): Duration {
    val millis = duration.toLong(DurationUnit.MILLISECONDS)
    val truncated = (millis / 10L) * 10L
    return truncated.toDuration(DurationUnit.MILLISECONDS)
}

fun makeTarget(allowedChars: List<Char>, len: Int): String {
    if (allowedChars.size < 2) {
        throw IllegalArgumentException("List of allowed characters must have at least two elements, but found only ${allowedChars.size}.")
    }
    var target = ""
    for (i in 1..len) {
        val prev: Char? = if (target.isEmpty()) {
            null
        } else {
            target.last()
        }
        var next: Char
        do {
            next = allowedChars.random()
        } while (next == prev)
        target += next
    }
    return target
}
