package me.arthurnagy.downtime.feature.overview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import me.arthurnagy.downtime.OverviewBinding
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.feature.shared.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class OverviewFragment : DowntimeFragment<OverviewBinding, OverviewViewModel>() {

    override val binding: OverviewBinding by binding(R.layout.fragment_overview)
    override val viewModel: OverviewViewModel by viewModel()

    override fun onCreateView() {
        binding.overviewChart.setAppSelectionListener { selectedApp ->
            if (selectedApp == null) {
                findNavController().navigate(OverviewFragmentDirections.actionOverviewFragmentToDashboardFragment(UsageType.SOT))
            } else {
                findNavController().navigate(OverviewFragmentDirections.actionOverviewFragmentToDetailFragment(UsageType.SOT, selectedApp.packageName))
            }
        }

        viewModel.event.observe(viewLifecycleOwner) { event ->
            event?.consume()?.let {
                when (it) {
                    is OverviewViewModel.Overview.Dashboard -> findNavController().navigate(
                        OverviewFragmentDirections.actionOverviewFragmentToDashboardFragment(it.usageType)
                    )
                    is OverviewViewModel.Overview.Detail -> findNavController().navigate(
                        OverviewFragmentDirections.actionOverviewFragmentToDetailFragment(UsageType.SOT, it.appPackageName)
                    )
                }.exhaustive
            }
        }
        viewModel.appEntries.observeNonNull(viewLifecycleOwner) {
            binding.overviewChart.submitData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNightLight()
        setupDoNotDisturb()
        setupManageAppNotifications()
    }

    private fun setupNightLight() {
        isSettingsActionAvailable("android.settings.NIGHT_DISPLAY_SETTINGS", doIfAvailable = { nightLightIntent ->
            binding.nightLight.setOnClickListener {
                startActivity(nightLightIntent)
            }
        }, doIfNotAvailable = { binding.nightLight.isVisible = false })
    }

    private fun setupDoNotDisturb() {
        val zenModeSettingsIntent = Intent("android.settings.ZEN_MODE_SETTINGS")
        val zenModePrioritySettingsIntent = Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS")
        val doNotDisturbFallback = { binding.doNotDisturb.isVisible = false }
        val doNotDisturbAction: (settingsIntent: Intent) -> Unit = { doNotDisturbIntent ->
            binding.doNotDisturb.setOnClickListener {
                startActivity(doNotDisturbIntent)
            }
        }
        when {
            zenModeSettingsIntent.resolveActivity(requireAppCompatActivity().packageManager) != null -> doNotDisturbAction(zenModeSettingsIntent)
            zenModePrioritySettingsIntent.resolveActivity(requireAppCompatActivity().packageManager) != null -> doNotDisturbAction(zenModeSettingsIntent)
            else -> doNotDisturbFallback()
        }
    }

    private fun setupManageAppNotifications() {
        isSettingsActionAvailable("android.settings.ALL_APPS_NOTIFICATION_SETTINGS", doIfAvailable = { manageNotificationsIntent ->
            binding.manageNotifications.setOnClickListener {
                startActivity(manageNotificationsIntent)
            }
        }, doIfNotAvailable = { binding.manageNotifications.isVisible = false })
    }

    private fun isSettingsActionAvailable(settingsAction: String, doIfAvailable: (settingsIntent: Intent) -> Unit, doIfNotAvailable: () -> Unit): Boolean {
        val settingsIntent = Intent(settingsAction)
        return if (settingsIntent.resolveActivity(requireAppCompatActivity().packageManager) != null) {
            doIfAvailable(settingsIntent)
            true
        } else {
            doIfNotAvailable()
            false
        }
    }

    override fun provideToolbar(): Toolbar = binding.toolbar

}