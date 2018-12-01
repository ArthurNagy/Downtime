package me.arthurnagy.downtime.core

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module.module

val appModule = module {

    factory {
        AppDispatchers(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            computation = Dispatchers.Default
        )
    }

}