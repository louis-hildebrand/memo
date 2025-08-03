package com.louishildebrand.memo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.time.Duration

@Composable
fun DurationMessage(
    memoDuration: Duration,
    recallDuration: Duration,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "(memo)",
                modifier = Modifier.alignByBaseline(),
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                displayDuration(memoDuration),
                modifier = Modifier.alignByBaseline(),
                fontSize = 52.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "(recall)",
                modifier = Modifier.alignByBaseline(),
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                displayDuration(recallDuration),
                modifier = Modifier.alignByBaseline(),
                fontSize = 52.sp
            )
        }
        HorizontalDivider(
            thickness = 3.dp,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "(total)",
                modifier = Modifier.alignByBaseline(),
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                displayDuration(memoDuration + recallDuration),
                modifier = Modifier.alignByBaseline(),
                fontSize = 52.sp
            )
        }
    }
}

fun displayDuration(duration: Duration): String {
    duration.toComponents { days, hours, minutes, seconds, nanoseconds ->
        val hundredths = nanoseconds / 10_000_000
        val totalHours = 24 * days + hours
        if (totalHours == 0L && minutes == 0) {
            return String.format(Locale.US, "%d.%02d", seconds, hundredths)
        } else if (totalHours == 0L) {
            return String.format(Locale.US, "%d:%02d.%02d", minutes, seconds, hundredths)
        } else {
            return String.format(
                Locale.US,
                "%d:%02d:%02d.%02d",
                totalHours,
                minutes,
                seconds,
                hundredths
            )
        }
    }
}
