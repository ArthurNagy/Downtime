package me.arthurnagy.downtime.feature.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.arthurnagy.downtime.core.AppDispatchers
import me.arthurnagy.downtime.core.AppUsage
import me.arthurnagy.downtime.core.StatsRepository
import me.arthurnagy.downtime.feature.overview.OverviewViewModel
import me.arthurnagy.downtime.feature.shared.DowntimeViewModel
import me.arthurnagy.downtime.feature.shared.Event
import me.arthurnagy.downtime.feature.shared.UsageType

class DashboardViewModel(usageType: UsageType, appDispatchers: AppDispatchers, private val statsRepository: StatsRepository) :
    DowntimeViewModel(appDispatchers) {

    var usageType: UsageType = usageType
        private set

    private val _event = MutableLiveData<Event<OverviewViewModel.Overview>>()
    val event: LiveData<Event<OverviewViewModel.Overview>> get() = _event

    private val _apps = MutableLiveData<List<AppUsage>>()
    val apps: LiveData<List<AppUsage>> get() = _apps

    init {
        Log.d("LOFASZ", "DashboardViewModel: init")
        launch {
            _apps.value = withContext(appDispatchers.computation) { sortAppUsage(usageType, statsRepository.getTodaysAppUsage()) }
        }
    }

    fun updateUsageType(usageType: UsageType) {
        this.usageType = usageType
        _apps.value = sortAppUsage(usageType, _apps.value)
    }

    private fun sortAppUsage(usageType: UsageType, appUsageList: List<AppUsage>?): List<AppUsage>? = appUsageList?.sortedByDescending {
        when (usageType) {
            UsageType.SOT -> it.screenTime
            UsageType.UNLOCKS_OPENS -> it.timesOpened.toLong()
            UsageType.NOTIFICATIONS -> it.notificationsReceived.toLong()
        }
    }

}