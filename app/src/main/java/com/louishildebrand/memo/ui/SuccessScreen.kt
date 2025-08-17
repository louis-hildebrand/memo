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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.louishildebrand.memo.R
import kotlin.time.Duration

@Preview(showBackground = true)
@Composable
fun SuccessScreen(
    modifier: Modifier = Modifier,
    target: String = "ABCDEFGHIJKL",
    memoDuration: Duration = Duration.parse("42s 530ms"),
    recallDuration: Duration = Duration.parse("9s"),
    settings: MemoSettings = MemoSettings(),
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(140, 217, 140),
    ) {
        Column(
            modifier = Modifier.safeContentPadding().padding(16.dp),
            horizontalAlignment = AbsoluteAlignment.Left,
            verticalArrangement = Arrangement.Top,
        ) {
            ToSettingsButton(settings)
        }
        Column(
            modifier = Modifier.safeContentPadding().padding(72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                stringResource(R.string.success_message),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                target,
                fontSize = 32.sp,
                fontFamily = FontFamily.Monospace
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