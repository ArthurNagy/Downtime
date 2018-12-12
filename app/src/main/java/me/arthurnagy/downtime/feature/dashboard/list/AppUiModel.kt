package me.arthurnagy.downtime.feature.dashboard.list

import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.core.AppUsage
import me.arthurnagy.downtime.core.formatToDuration
import me.arthurnagy.downtime.feature.shared.StringProvider
import me.arthurnagy.downtime.feature.shared.UsageType

data class AppUiModel(val usageType: UsageType, val appUsage: AppUsage, val stringProvider: StringProvider) {

    val usageLabel = when (usageType) {
        UsageType.SOT -> appUsage.screenTime.formatToDuration(stringProvider)
        UsageType.UNLOCKS_OPENS -> stringProvider.getString(R.string.app_opened_count, appUsage.timesOpened)
        UsageType.NOTIFICATIONS -> stringProvider.getString(R.string.app_notifications_count, appUsage.notificationsReceived)
    }

}