package com.louishildebrand.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.louishildebrand.memo.data.MemoSettings
import com.louishildebrand.memo.ui.SettingsScreen
import com.louishildebrand.memo.ui.SettingsViewModel
import com.louishildebrand.memo.ui.theme.MemoTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory }

        setContent {
            MemoTheme {
                SettingsScreen(viewModel)
            }
        }
    }
}