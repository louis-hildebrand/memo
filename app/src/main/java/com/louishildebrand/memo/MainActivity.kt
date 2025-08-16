package com.louishildebrand.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.louishildebrand.memo.ui.MemoApp
import com.louishildebrand.memo.ui.MemoViewModel
import com.louishildebrand.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: MemoViewModel by viewModels()
        setContent {
            MemoTheme {
                MemoApp(viewModel)
            }
        }
    }
}
