package me.arthurnagy.downtime.core

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StatsRepository(private val usageStatsManager: UsageStatsManager) {

    private val startTimeToday: Long
    private val endTimeToday: Long

    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        endTimeToday = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        startTimeToday = calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getTodaysUnlockCount() = suspendCoroutine<Int> {
        val usageEvents = usageStatsManager.queryEvents(startTimeToday, endTimeToday)
        var count = 0
        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                count++
            }
        }
        it.resume(count)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getTodaysNotificationCount() = suspendCoroutine<Int> {
        val usageEvents = usageStatsManager.queryEvents(startTimeToday, endTimeToday)
        var count = 0
        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)
            if (event.eventType == 12 && event.packageName != "android") {// UsageEvents.Event.NOTIFICATION_INTERRUPTION
                count++
            }
        }
        it.resume(count)
    }

}