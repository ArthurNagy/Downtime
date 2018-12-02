package me.arthurnagy.downtime.core

import android.app.usage.UsageStatsManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module.module

val appModule = module {

    single {
        AppDispatchers(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            computation = Dispatchers.Default
        )
    }


    factory {
        get<Context>().getSystemService("usagestats") as UsageStatsManager
    }

    single {
        StatsRepository(get())
    }

}