package me.arthurnagy.downtime.core

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import me.arthurnagy.downtime.BuildConfig
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StatsRepository(private val usageStatsManager: UsageStatsManager, private val packageManager: PackageManager) {

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
        val today = Date(startTimeToday)
        val tomorrow = Date(endTimeToday)
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault(Locale.Category.FORMAT))
        println("LOFASZ: today: ${formatter.format(today)} tomorrow: ${formatter.format(tomorrow)}")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getTodaysUnlockCount() = suspendCoroutine<Int> { continuation ->
        val count = usageStatsManager.queryEvents(startTimeToday, endTimeToday)
            .events
            .asSequence()
            .filter { it.eventType == UsageEvents.Event.KEYGUARD_HIDDEN }
            .count()
        continuation.resume(count)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getTodaysNotificationCount() = suspendCoroutine<Int> { continuation ->
        val count = usageStatsManager.queryEvents(startTimeToday, endTimeToday)
            .events
            .asSequence()
            .filter { it.eventType == EVENT_TYPE_NOTIFICATION_INTERRUPTION && it.packageName != ANDROID_NOTIFICATIONS_PACKAGE }
            .count()
        continuation.resume(count)
    }

    suspend fun getTodaysAppUsage() = suspendCoroutine<List<AppUsage>> { continuation ->
        val appUsageList = usageStatsManager.queryAndAggregateUsageStats(startTimeToday, endTimeToday)
            .filter { !isThisApp(it.key) && !isExcludedSystemApp(it.key) && it.value.lastTimeUsed > startTimeToday }
            .map { (key, entry) ->
                val appInfo = packageManager.getApplicationInfo(key, PackageManager.GET_META_DATA)
                val appNotificationsCount = usageStatsManager.queryEvents(startTimeToday, endTimeToday)
                    .events
                    .filter { it.packageName == key && it.eventType == EVENT_TYPE_NOTIFICATION_INTERRUPTION }
                    .count()
                AppUsage(
                    name = packageManager.getApplicationLabel(appInfo).toString(),
                    packageName = key,
                    screenTime = entry.totalTimeInForeground,
                    timesOpened = entry.appLaunchCount,
                    notificationsReceived = appNotificationsCount
                )
            }
        continuation.resume(appUsageList)
    }

    private fun isThisApp(packageName: String) = packageName.contains(BuildConfig.APPLICATION_ID)

    private fun isExcludedSystemApp(packageName: String) = packageName.contains("launcher") || packageName.contains("systemui")

    companion object {
        private const val EVENT_TYPE_NOTIFICATION_INTERRUPTION = 12
        private const val ANDROID_NOTIFICATIONS_PACKAGE = "android"
    }

}