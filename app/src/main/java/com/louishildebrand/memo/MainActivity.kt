package com.louishildebrand.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.louishildebrand.memo.ui.theme.MemoTheme

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
    data class Memo(val target: String, val idx: Int) : AppState
    data class Check(val target: String, val guess: String) : AppState
    data object Success : AppState
    data class Failure(val target: String, val guess: String) : AppState
}

@Composable
fun MemoApp(allowedChars: List<Char> = ALLOWED_CHARS, len: Int = 8) {
    // TODO: Fix colours to work in both light and dark modes
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
                        state = AppState.Memo(target = makeTarget(allowedChars, len), idx = 0)
                    }
                ) {
                    StartScreen()
                }
            is AppState.Memo -> {
                val s = state as AppState.Memo
                Surface(
                    onClick = {
                        state = if (s.idx + 1 == s.target.length) {
                            AppState.Check(target = s.target, guess = "")
                        } else {
                            AppState.Memo(target = s.target, idx = s.idx + 1)
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
                    currentGuess = s.guess,
                    update = { guess ->
                        state = AppState.Check(target = s.target, guess = guess.uppercase())
                    },
                    submit = {
                        state = if (s.guess.uppercase() == s.target.uppercase()) {
                            AppState.Success
                        } else {
                            AppState.Failure(target = s.target, guess = s.guess)
                        }
                    }
                )
            }
            AppState.Success -> {
                Surface(
                    onClick = {
                        state = AppState.Memo(target = makeTarget(allowedChars, len), idx = 0)
                    }
                ) {
                    SuccessScreen()
                }
            }
            is AppState.Failure -> {
                Surface(
                    onClick = {
                        state = AppState.Memo(target = makeTarget(allowedChars, len), idx = 0)
                    }
                ) {
                    FailureScreen(
                        target = (state as AppState.Failure).target,
                        guess = (state as AppState.Failure).guess
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
fun SuccessScreen(modifier: Modifier = Modifier) {
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
            StartMessage()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FailureScreen(
        modifier: Modifier = Modifier,
        target: String = "ABCDEFGHIJKL",
        guess: String = "ADCBEFGHIJK"
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
                                withStyle(style = SpanStyle(color = Color(130, 130, 125))) {
                                    append(c.toString())
                                }
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
                                withStyle(style = SpanStyle(color = Color(130, 130, 125))) {
                                    append(c.toString())
                                }
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
            StartMessage()
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
