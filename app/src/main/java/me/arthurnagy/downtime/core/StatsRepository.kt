package me.arthurnagy.downtime.core

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.app.usage.UsageStatsManager.INTERVAL_DAILY
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StatsRepository(private val usageStatsManager: UsageStatsManager) {

    private val startTime: Long
    private val endTime: Long
    private val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        startTime = calendar.timeInMillis
        println("LOFASZ: begin: ${formatter.format(Date(startTime))}, end: ${formatter.format(Date(endTime))}")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getTodaysUnlockCount() = suspendCoroutine<Int> { continuation ->
        val count = usageStatsManager.queryEventStats(INTERVAL_DAILY, startTime, endTime)
            .filter { it.eventType == UsageEvents.Event.SCREEN_INTERACTIVE }
            .sumBy { it.count }
        continuation.resume(count)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getTodaysNotificationCount() = suspendCoroutine<Int> {
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        var count = 0
        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)
            if (event.eventType == 12) {// UsageEvents.Event.NOTIFICATION_INTERRUPTION
                count++
            }
        }
        it.resume(count)
    }

}