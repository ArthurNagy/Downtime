package me.arthurnagy.downtime.feature.dashboard

import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import me.arthurnagy.downtime.DashboardBinding
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.feature.dashboard.usage.filter.UsageSpinnerAdapter
import me.arthurnagy.downtime.feature.shared.DowntimeFragment
import me.arthurnagy.downtime.feature.shared.binding
import me.arthurnagy.downtime.feature.shared.observeNonNull
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DashboardFragment : DowntimeFragment<DashboardBinding, DashboardViewModel>() {

    override val binding: DashboardBinding by binding(R.layout.fragment_dashboard)
    override val viewModel: DashboardViewModel by viewModel { parametersOf(DashboardFragmentArgs.fromBundle(arguments!!).usageType) }

    override fun onCreateView() {
        val adapter = UsageSpinnerAdapter(requireContext())
        binding.usageSpinner.adapter = adapter
        binding.usageSpinner.setSelection(adapter.getPositionForUsageType(viewModel.usageType))
        binding.usageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.updateUsageType(adapter.getUsageType(position))
            }

        }

        binding.appUsageRecycler.setItemClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToDetailFragment(viewModel.usageType, it.packageName))
        }

        viewModel.apps.observeNonNull(viewLifecycleOwner) {
            binding.appUsageRecycler.submitAppList(viewModel.usageType, it)
        }
    }

    override fun provideToolbar(): Toolbar = binding.toolbar

}