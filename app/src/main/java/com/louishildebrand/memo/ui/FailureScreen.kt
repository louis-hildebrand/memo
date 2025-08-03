package com.louishildebrand.memo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.louishildebrand.memo.R
import kotlin.time.Duration

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