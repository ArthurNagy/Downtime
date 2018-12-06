package me.arthurnagy.downtime.core

import android.app.usage.UsageStats
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
            .filter { it.eventType == EVENT_TYPE_UNLOCK }
            .count()
        continuation.resume(count)
    }

    suspend fun getTodaysNotificationCount() = suspendCoroutine<Int> { continuation ->
        val count = usageStatsManager.queryEvents(startTimeToday, endTimeToday)
            .events
            .asSequence()
            .filter { it.eventType == EVENT_TYPE_NOTIFICATION && it.packageName != ANDROID_NOTIFICATIONS_PACKAGE }
            .count()
        continuation.resume(count)
    }

    suspend fun getTodaysAppUsage() = suspendCoroutine<List<AppUsage>> { continuation ->
        val appUsageList = usageStatsManager.queryAndAggregateUsageStats(startTimeToday, endTimeToday)
            .filter { !isThisApp(it.key) && !isExcludedSystemApp(it.key) && it.value.lastTimeUsed > startTimeToday && packageManager.isAppInstalled(it.key) }
            .map(::transformUsageStatsToAppUsage)
        continuation.resume(appUsageList)
    }

    private fun transformUsageStatsToAppUsage(usageStats: Map.Entry<String, UsageStats>): AppUsage {
        val appInfo = packageManager.getApplicationInfo(usageStats.key, PackageManager.GET_META_DATA)
        val appNotificationsCount = usageStatsManager.queryEvents(startTimeToday, endTimeToday)
            .events
            .filter { it.packageName == usageStats.key && it.eventType == EVENT_TYPE_NOTIFICATION }
            .count()
        return AppUsage(
            name = packageManager.getApplicationLabel(appInfo).toString(),
            packageName = usageStats.key,
            screenTime = usageStats.value.totalTimeInForeground,
            timesOpened = usageStats.value.appLaunchCount,
            notificationsReceived = appNotificationsCount
        )
    }

    private fun PackageManager.isAppInstalled(packageName: String) = try {
        this.getApplicationInfo(packageName, 0).enabled
    } catch (exception: Exception) {
        false
    }

    private fun isThisApp(packageName: String) = packageName.contains(BuildConfig.APPLICATION_ID)

    private fun isExcludedSystemApp(packageName: String) = packageName.contains("launcher") || packageName.contains("systemui")

    companion object {
        private const val EVENT_TYPE_UNLOCK = 18
        private const val EVENT_TYPE_NOTIFICATION = 12
        private const val ANDROID_NOTIFICATIONS_PACKAGE = "android"
    }

}