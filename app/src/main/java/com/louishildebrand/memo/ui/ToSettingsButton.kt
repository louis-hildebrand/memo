package com.louishildebrand.memo.ui

import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.louishildebrand.memo.SettingsActivity
import com.louishildebrand.memo.data.MemoSettings

@Composable
fun ToSettingsButton() {
   val context = LocalContext.current
   Button(
       onClick = {
           val intent = Intent(context, SettingsActivity::class.java)
           context.startActivity(intent)
       }
   ) {
       Text("Settings")
   }
}
