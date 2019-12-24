package me.arthurnagy.downtime

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import me.arthurnagy.downtime.core.appModule
import me.arthurnagy.downtime.feature.dashboard.dashboardModule
import me.arthurnagy.downtime.feature.detail.detailModule
import me.arthurnagy.downtime.feature.overview.overviewModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DowntimeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin {
            androidLogger()
            androidContext(this@DowntimeApp)
            modules(appModule + overviewModule + dashboardModule + detailModule)
        }
    }

}