package me.arthurnagy.downtime

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import me.arthurnagy.downtime.core.appModule
import me.arthurnagy.downtime.feature.dashboard.dashboardModule
import me.arthurnagy.downtime.feature.detail.detailModule
import me.arthurnagy.downtime.feature.overview.overviewModule
import org.koin.android.ext.android.startKoin

class DowntimeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin(this, listOf(appModule, overviewModule, dashboardModule, detailModule))
    }

}