package com.louishildebrand.memo.ui

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.louishildebrand.memo.MainActivity
import com.louishildebrand.memo.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state: SettingsState by viewModel.state.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = modifier.safeContentPadding().padding(16.dp)
    ) {
        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("settings", viewModel.settings)
                context.startActivity(intent)
            }
        ) {
            Text("Back")
        }
        Column {
            Text(stringResource(R.string.allowed_chars))
            TextField(
                state.allowedCharsString,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
                ),
                onValueChange = { newAllowedChars ->
                    viewModel.updateAllowedChars(newAllowedChars)
                }
            )
        }
        Column {
            Text(stringResource(R.string.target_len))
            TextField(
                state.targetLenString,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { newLen ->
                    viewModel.updateTargetLen(newLen)
                }
            )
        }
    }
}