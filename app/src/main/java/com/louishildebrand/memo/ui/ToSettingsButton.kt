package com.louishildebrand.memo.ui

import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.louishildebrand.memo.SettingsActivity

@Composable
fun ToSettingsButton(settings: MemoSettings) {
   val context = LocalContext.current
   Button(
       onClick = {
           val intent = Intent(context, SettingsActivity::class.java)
           intent.putExtra("settings", settings)
           context.startActivity(intent)
       }
   ) {
       Text("Settings")
   }
}
