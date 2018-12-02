package me.arthurnagy.downtime.feature.dashboard

import androidx.appcompat.widget.Toolbar
import me.arthurnagy.downtime.DashboardBinding
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.feature.shared.DowntimeFragment
import me.arthurnagy.downtime.feature.shared.binding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : DowntimeFragment<DashboardBinding, DashboardViewModel>() {

    override val binding: DashboardBinding by binding(R.layout.fragment_dashboard)
    override val viewModel: DashboardViewModel by viewModel()

    override fun onCreateView() {
    }

    override fun provideToolbar(): Toolbar = binding.toolbar

}