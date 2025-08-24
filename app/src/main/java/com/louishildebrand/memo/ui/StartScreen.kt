package com.louishildebrand.memo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.louishildebrand.memo.R
import com.louishildebrand.memo.data.MemoSettings

@Preview(showBackground = true)
@Composable
fun StartScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(140, 140, 217)
    ) {
        Column(
            modifier = Modifier.safeContentPadding().padding(16.dp),
            horizontalAlignment = AbsoluteAlignment.Left,
            verticalArrangement = Arrangement.Top,
        ) {
            ToSettingsButton()
        }
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
