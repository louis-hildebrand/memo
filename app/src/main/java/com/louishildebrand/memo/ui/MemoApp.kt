package com.louishildebrand.memo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MemoApp(viewModel: MemoViewModel) {
    // TODO: Fix colours to work in both light and dark modes
    // TODO: Be more consistent with fonts (https://developer.android.com/codelabs/jetpack-compose-theming#5)
    // TODO: Let user choose allowed characters, memo length
    // TODO: Save results, show stats
    val state: AppState by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            AppState.Start ->
                Surface(
                    onClick = { viewModel.start() }
                ) {
                    StartScreen(settings = viewModel.settings)
                }

            is AppState.Memo -> {
                val s = state as AppState.Memo
                Surface(
                    onClick = { viewModel.nextMemoLetter() },
                ) {
                    MemoScreen(c = s.target[s.idx])
                }
            }

            is AppState.Check -> {
                val s = state as AppState.Check
                // TODO: Surely there's a better way
                CheckScreen(
                    currentGuess = s.partialGuess,
                    update = { guess -> viewModel.updateGuess(guess) },
                    submit = { viewModel.submitGuess() }
                )
            }

            is AppState.Success -> {
                val s = state as AppState.Success
                Surface(
                    onClick = { viewModel.start() }
                ) {
                    SuccessScreen(
                        target = s.target,
                        memoDuration = s.memoDuration,
                        recallDuration = s.recallDuration
                    )
                }
            }

            is AppState.Failure -> {
                val s = state as AppState.Failure
                Surface(
                    onClick = { viewModel.start() }
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
