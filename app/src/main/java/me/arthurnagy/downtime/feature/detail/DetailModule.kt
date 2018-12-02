package me.arthurnagy.downtime.feature.detail

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val detailModule = module {
    viewModel { DetailViewModel(get()) }
}