package me.arthurnagy.downtime.feature.dashboard

import androidx.appcompat.widget.Toolbar
import me.arthurnagy.downtime.DashboardBinding
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.feature.shared.DowntimeFragment
import me.arthurnagy.downtime.feature.shared.binding
import me.arthurnagy.downtime.feature.shared.observeNonNull
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DashboardFragment : DowntimeFragment<DashboardBinding, DashboardViewModel>() {

    override val binding: DashboardBinding by binding(R.layout.fragment_dashboard)
    override val viewModel: DashboardViewModel by viewModel { parametersOf(DashboardFragmentArgs.fromBundle(arguments).usageType) }

    override fun onCreateView() {
        viewModel.apps.observeNonNull(viewLifecycleOwner) {
            binding.appUsageRecycler.submitAppList(viewModel.usageType, it)
        }
    }

    override fun provideToolbar(): Toolbar = binding.toolbar

}