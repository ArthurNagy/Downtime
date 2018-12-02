package me.arthurnagy.downtime.feature.overview

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val overviewModule = module {
    viewModel { OverviewViewModel(get(), get()) }
}