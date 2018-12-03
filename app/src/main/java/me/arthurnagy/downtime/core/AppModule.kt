package me.arthurnagy.downtime.core

import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module.module

@SuppressLint("WrongConstant")
val appModule = module {

    single {
        AppDispatchers(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            computation = Dispatchers.Default
        )
    }

    factory { get<Context>().getSystemService("usagestats") as UsageStatsManager }

    factory { get<Context>().packageManager as PackageManager }

    single { StatsRepository(get(), get()) }

}