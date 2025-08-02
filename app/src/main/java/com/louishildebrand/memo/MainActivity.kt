package com.louishildebrand.memo

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.louishildebrand.memo.ui.theme.MemoTheme
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.toDuration

val ALLOWED_CHARS = ('A'..'B') + ('D'..'X')

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoTheme {
                MemoApp()
            }
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun StartScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(140, 140, 217)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StartMessage()
        }
    }
}

@Composable
fun StartMessage() {
    Text(
        stringResource(R.string.tap_to_start),
        fontSize = 32.sp,
        fontStyle = FontStyle.Italic
    )
}

@Composable
fun DurationMessage(
    memoDuration: Duration,
    recallDuration: Duration,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "(memo)",
                modifier = Modifier.alignByBaseline(),
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                displayDuration(memoDuration),
                modifier = Modifier.alignByBaseline(),
                fontSize = 52.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "(recall)",
                modifier = Modifier.alignByBaseline(),
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                displayDuration(recallDuration),
                modifier = Modifier.alignByBaseline(),
                fontSize = 52.sp
            )
        }
        HorizontalDivider(
            thickness = 3.dp,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "(total)",
                modifier = Modifier.alignByBaseline(),
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                displayDuration(memoDuration + recallDuration),
                modifier = Modifier.alignByBaseline(),
                fontSize = 52.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemoScreen(modifier: Modifier = Modifier, c: Char = 'A') {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(140, 140, 217)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = c.toString(),
                textAlign = TextAlign.Center,
                fontSize = 110.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckScreen(
    modifier: Modifier = Modifier,
    currentGuess: String = "ABCD",
    update: ((String) -> Unit)? = null,
    submit: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(140, 140, 217)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                currentGuess,
                onValueChange = { newGuess ->
                    if (update != null) {
                        update(newGuess)
                    }
                },
                singleLine = true,
                textStyle = TextStyle.Default.copy(fontSize = 28.sp)
            )
            Spacer(modifier = Modifier.size(24.dp))
            Button(
                onClick = {
                    if (submit != null) {
                        submit()
                    }
                },
            ) {
                Text(
                    text = stringResource(R.string.check),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessScreen(
        modifier: Modifier = Modifier,
        memoDuration: Duration = Duration.parse("42s 530ms"),
        recallDuration: Duration = Duration.parse("9s")
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(140, 217, 140),
    ) {
        Column(
            modifier = Modifier.safeContentPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                stringResource(R.string.success_message),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DurationMessage(memoDuration = memoDuration, recallDuration = recallDuration)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FailureScreen(
        modifier: Modifier = Modifier,
        target: String = "ABCDEFGHIJKL",
        guess: String = "ADCBEFGHIJK",
        memoDuration: Duration = Duration.parse("1m 5s 200ms"),
        recallDuration: Duration = Duration.parse("10s 123ms")
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(217, 140, 140),
    ) {
        Column(
            modifier = Modifier.safeContentPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                stringResource(R.string.failure_message),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
            )
            Column {
                Spacer(modifier = Modifier.size(24.dp))
                Text(
                    stringResource(R.string.target),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    buildAnnotatedString {
                        target.forEachIndexed { i, c ->
                            val matches = guess.length > i && guess[i] == c
                            if (matches) {
                                append(c.toString())
                            } else {
                                withStyle(style = SpanStyle(color = Color(180, 0, 0))) {
                                    append(c.toString())
                                }
                            }
                        }
                    },
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    stringResource(R.string.your_answer),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    buildAnnotatedString {
                        guess.forEachIndexed { i, c ->
                            val matches = target.length > i && target[i] == c
                            if (matches) {
                                append(c.toString())
                            } else {
                                withStyle(style = SpanStyle(color = Color(180, 0, 0))) {
                                    append(c.toString())
                                }
                            }
                        }
                    },
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DurationMessage(memoDuration = memoDuration, recallDuration = recallDuration)
        }
    }
}

fun truncateDuration(duration: Duration): Duration {
    val millis = duration.toLong(DurationUnit.MILLISECONDS)
    val truncated = (millis / 10L) * 10L
    return truncated.toDuration(DurationUnit.MILLISECONDS)
}

fun displayDuration(duration: Duration): String {
    duration.toComponents { days, hours, minutes, seconds, nanoseconds ->
        val hundredths = nanoseconds / 10_000_000
        val totalHours = 24 * days + hours
        if (totalHours == 0L && minutes == 0) {
            return String.format(Locale.US, "%d.%02d", seconds, hundredths)
        } else if (totalHours == 0L) {
            return String.format(Locale.US, "%d:%02d.%02d", minutes, seconds, hundredths)
        } else {
            return String.format(
                Locale.US,
                "%d:%02d:%02d.%02d",
                totalHours,
                minutes,
                seconds,
                hundredths
            )
        }
    }
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
