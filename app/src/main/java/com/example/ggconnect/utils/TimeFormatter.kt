package com.example.ggconnect.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatter {

    /**
     * Formats a timestamp to a relative time string (e.g., "2h ago", "Yesterday").
     */
    fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minutes = diff / (60 * 1000)
        val hours = diff / (60 * 60 * 1000)
        val days = diff / (24 * 60 * 60 * 1000)

        return when {
            days > 0 -> "$days d ago"
            hours > 0 -> "$hours h ago"
            minutes > 0 -> "$minutes m ago"
            else -> "Just now"
        }
    }

    /**
     * Formats a timestamp to a full date string (e.g., "dd/MM/yyyy hh:mm a").
     */
    fun formatFullDate(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }
}
