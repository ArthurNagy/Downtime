package me.arthurnagy.downtime.feature.dashboard

import me.arthurnagy.downtime.feature.shared.UsageType
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val dashboardModule = module {
    viewModel { (usageType: UsageType) -> DashboardViewModel(usageType, get(), get()) }
}