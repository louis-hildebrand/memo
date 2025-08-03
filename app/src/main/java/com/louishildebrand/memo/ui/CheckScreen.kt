package com.louishildebrand.memo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.louishildebrand.memo.R

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