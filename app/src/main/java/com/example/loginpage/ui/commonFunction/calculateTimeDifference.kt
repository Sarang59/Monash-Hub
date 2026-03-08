package com.example.loginpage.ui.commonFunction

import androidx.compose.runtime.Composable
import com.google.firebase.Timestamp
import java.util.Date
import java.util.concurrent.TimeUnit

//Returns a human-readable string representing the time difference between the given Firebase timestamp and the current time.
@Composable
fun calculateTimeDifference(timestamp: Timestamp?): String {
    // Convert Firebase timestamp to Java Date (null-safe)
    val postDate = timestamp?.toDate()
    // Get current time
    val now = Date()

    // Check difference in days first
    val diffInMillis = now.time - (postDate?.time ?: 0)
    var difference = TimeUnit.MILLISECONDS.toDays(diffInMillis)
    var returnStr = "${difference} days ago"

    if(difference < 1) {
        difference = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        returnStr = "${difference} hrs ago"
    }

    if(difference < 1) {
        difference = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        returnStr = "${difference} minutes ago"
    }

    if(difference < 1) {
        return "Recent"
    }

    return returnStr
}