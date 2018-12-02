package me.arthurnagy.downtime.feature.detail

import androidx.appcompat.widget.Toolbar
import me.arthurnagy.downtime.DetailBinding
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.feature.shared.DowntimeFragment
import me.arthurnagy.downtime.feature.shared.binding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : DowntimeFragment<DetailBinding, DetailViewModel>() {

    override val binding: DetailBinding by binding(R.layout.fragment_detail)
    override val viewModel: DetailViewModel by viewModel()

    override fun onCreateView() {
    }

    override fun provideToolbar(): Toolbar = binding.toolbar

}