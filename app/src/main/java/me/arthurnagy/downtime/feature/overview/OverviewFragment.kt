package me.arthurnagy.downtime.feature.overview

import androidx.appcompat.widget.Toolbar
import me.arthurnagy.downtime.OverviewBinding
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.feature.shared.DowntimeFragment
import me.arthurnagy.downtime.feature.shared.binding
import org.koin.androidx.viewmodel.ext.android.viewModel

class OverviewFragment : DowntimeFragment<OverviewBinding, OverviewViewModel>() {

    override val binding: OverviewBinding by binding(R.layout.fragment_overview)
    override val viewModel: OverviewViewModel by viewModel()

    override fun onCreateView() {

    }

    override fun provideToolbar(): Toolbar = binding.toolbar
}