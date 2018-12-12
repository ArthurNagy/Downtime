package me.arthurnagy.downtime.feature.dashboard.list

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.BatchingListUpdateCallback

typealias OnUpdateFinished = () -> Unit

class AppUsageListUpdateCallback(appUsageAdapter: AppUsageAdapter) :
    BatchingListUpdateCallback(AdapterListUpdateCallback(appUsageAdapter)) {

    private var updateFinishedCallback: OnUpdateFinished? = null

    fun setUpdateFinishedCallback(updateFinishedCallback: OnUpdateFinished) {
        this.updateFinishedCallback = updateFinishedCallback
    }

    override fun dispatchLastEvent() {
        super.dispatchLastEvent()
        updateFinishedCallback?.invoke()
    }

}