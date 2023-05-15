package id.fishku.fishersellercore.util

import android.text.format.DateUtils
import id.fishku.fishersellercore.util.Constants.TIME_IN_MILLIS
import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtils {

    private val onlyDate = SimpleDateFormat("d MMM", Locale.US)

    fun getFormattedTime(timeInMillis: Long): String {
        val date = Date(timeInMillis * TIME_IN_MILLIS)

        return when {
            isToday(date) -> "Hari ini"
            isYesterday(date) -> "Kemarin"
            else -> onlyDate.format(date) ?: "Lalu"
        }
    }

    fun getFormattedTimeChatLog(timeInMillis: Long): String {
        val date = Date(timeInMillis * TIME_IN_MILLIS)
        val fullFormattedTime = SimpleDateFormat("d MMM, h:mm a", Locale.US)
        val onlyTime = SimpleDateFormat("h:mm a", Locale.US)

        return when {
            isToday(date) -> onlyTime.format(date)
            else -> fullFormattedTime.format(date)
        }

    }

    private fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    private fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }
}