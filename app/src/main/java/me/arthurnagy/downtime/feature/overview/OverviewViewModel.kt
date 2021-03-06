package me.arthurnagy.downtime.feature.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.arthurnagy.downtime.core.AppDispatchers
import me.arthurnagy.downtime.core.AppUsage
import me.arthurnagy.downtime.core.StatsRepository
import me.arthurnagy.downtime.feature.shared.DowntimeViewModel
import me.arthurnagy.downtime.feature.shared.Event
import me.arthurnagy.downtime.feature.shared.UsageType
import me.arthurnagy.downtime.feature.shared.mutableLiveDataOf

class OverviewViewModel(dispatchers: AppDispatchers, private val statsRepository: StatsRepository) : DowntimeViewModel(dispatchers) {

    private val _event = MutableLiveData<Event<Overview>>()
    val event: LiveData<Event<Overview>> get() = _event

    private val _unlocks = mutableLiveDataOf(0.toString())
    val unlocks: LiveData<String> get() = _unlocks

    private val _appEntries = MutableLiveData<List<AppUsage>>()
    val appEntries: LiveData<List<AppUsage>> get() = _appEntries

    private val _notifications = mutableLiveDataOf(0.toString())
    val notifications: LiveData<String> get() = _notifications

    fun load() {
        Log.d("LOFASZ", "OverviewViewModel:init")
        launch {
            _unlocks.value = withContext(dispatchers.io) { statsRepository.getTodaysUnlockCount().toString() }
            _notifications.value = withContext(dispatchers.io) { statsRepository.getTodaysNotificationCount().toString() }
            _appEntries.value = withContext(dispatchers.io) { statsRepository.getTodaysAppUsage() }
        }
    }

    fun onUnlocksClicked() {
        _event.value = Event(Overview.Dashboard(UsageType.UNLOCKS_OPENS))
    }

    fun onNotificationsClicked() {
        _event.value = Event(Overview.Dashboard(UsageType.NOTIFICATIONS))
    }

    fun onDashboardClicked() {
        _event.value = Event(Overview.Dashboard(UsageType.SOT))
    }

    sealed class Overview {
        data class Dashboard(val usageType: UsageType) : Overview()
        data class Detail(val appPackageName: String) : Overview()
    }

}