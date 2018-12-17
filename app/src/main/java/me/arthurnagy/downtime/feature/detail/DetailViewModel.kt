package me.arthurnagy.downtime.feature.detail

import android.util.Log
import me.arthurnagy.downtime.core.AppDispatchers
import me.arthurnagy.downtime.feature.shared.DowntimeViewModel

class DetailViewModel(appDispatchers: AppDispatchers) : DowntimeViewModel(appDispatchers) {

    init {
        Log.d("LOFASZ", "DetailViewModel:init")
    }

}