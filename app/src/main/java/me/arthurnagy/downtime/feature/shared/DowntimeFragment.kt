package me.arthurnagy.downtime.feature.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import me.arthurnagy.downtime.BR
import me.arthurnagy.downtime.R

abstract class DowntimeFragment<VB : ViewDataBinding, VM : DowntimeViewModel> : Fragment() {

    abstract val binding: VB
    abstract val viewModel: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.setVariable(BR.viewModel, viewModel)
        onCreateView()
        return binding.root
    }

    abstract fun onCreateView()

    abstract fun provideToolbar(): Toolbar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val navController = Navigation.findNavController(requireAppCompatActivity(), R.id.nav_host)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        requireAppCompatActivity().setSupportActionBar(provideToolbar())
        requireAppCompatActivity().setupActionBarWithNavController(navController, appBarConfiguration)
    }

}