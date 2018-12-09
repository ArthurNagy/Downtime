package me.arthurnagy.downtime.feature.dashboard.list

import androidx.databinding.ObservableField
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.core.AppUsage
import me.arthurnagy.downtime.core.formatToDuration
import me.arthurnagy.downtime.feature.shared.StringProvider
import me.arthurnagy.downtime.feature.shared.UsageType
import me.arthurnagy.downtime.feature.shared.dependantObservableField

class AppUiModel(private val stringProvider: StringProvider) {

    val appUsage = ObservableField<AppUsage>()
    val usageType = ObservableField<UsageType>()
    val usageLabel = dependantObservableField(appUsage, usageType) {
        val usageType = this.usageType.get()
        val appUsage = this.appUsage.get()
        when (usageType) {
            UsageType.SOT -> appUsage?.screenTime?.formatToDuration(stringProvider)
            UsageType.UNLOCKS_OPENS -> stringProvider.getString(R.string.app_opened_count, appUsage?.timesOpened ?: 0)
            UsageType.NOTIFICATIONS -> stringProvider.getString(R.string.app_notifications_count, appUsage?.notificationsReceived ?: 0)
            null -> ""
        }
    }

}