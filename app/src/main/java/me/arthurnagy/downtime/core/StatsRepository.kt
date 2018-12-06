package me.arthurnagy.downtime.core

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.pm.PackageManager
import me.arthurnagy.downtime.BuildConfig
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StatsRepository(private val usageStatsManager: UsageStatsManager, private val packageManager: PackageManager) {

    private val todayDateTime: LocalDateTime by lazy { LocalDateTime.now() }
    private val startTimeToday: Long by lazy { todayDateTime.truncatedTo(ChronoUnit.DAYS).toUtcMillis() }
    private val endTimeToday: Long by lazy { todayDateTime.plusDays(1).truncatedTo(ChronoUnit.DAYS).toUtcMillis() }

    suspend fun getTodaysUnlockCount() = suspendCoroutine<Int> { continuation ->
        val count = usageStatsManager.queryEvents(startTimeToday, endTimeToday)
            .events
            .asSequence()
            .filter { it.eventType == UsageEvents.Event.KEYGUARD_HIDDEN }
            .count()
        continuation.resume(count)
    }

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