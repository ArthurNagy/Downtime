package me.arthurnagy.downtime.feature.dashboard

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val dashboardModule = module {
    viewModel { DashboardViewModel(get()) }
}