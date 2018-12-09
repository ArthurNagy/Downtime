package me.arthurnagy.downtime.feature.dashboard.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.arthurnagy.downtime.AppUsageBinding
import me.arthurnagy.downtime.core.AppUsage
import me.arthurnagy.downtime.feature.shared.StringProvider
import me.arthurnagy.downtime.feature.shared.UsageType

class AppUsageAdapter : ListAdapter<AppUsage, AppUsageAdapter.AppUsageViewHolder>(diffItemCallback) {

    private var usageType: UsageType = UsageType.SOT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppUsageViewHolder = AppUsageViewHolder.create(parent)

    override fun onBindViewHolder(holder: AppUsageViewHolder, position: Int) = holder.bind(usageType, getItem(position))


    fun submitData(usageType: UsageType, apps: List<AppUsage>) {
        this.usageType = usageType
        submitList(apps)
    }

    class AppUsageViewHolder(private val binding: AppUsageBinding, stringProvider: StringProvider) : RecyclerView.ViewHolder(binding.root) {

        private val appUiModel = AppUiModel(stringProvider)

        init {
            binding.app = appUiModel
        }

        fun bind(usageType: UsageType, appUsage: AppUsage) {
            appUiModel.appUsage.set(appUsage)
            appUiModel.usageType.set(usageType)
            binding.executePendingBindings()
        }

        companion object {
            fun create(parent: ViewGroup) =
                AppUsageViewHolder(AppUsageBinding.inflate(LayoutInflater.from(parent.context), parent, false), StringProvider(parent.context))
        }

    }

    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<AppUsage>() {
            override fun areItemsTheSame(oldItem: AppUsage, newItem: AppUsage): Boolean = oldItem.packageName == newItem.packageName

            override fun areContentsTheSame(oldItem: AppUsage, newItem: AppUsage): Boolean = oldItem == newItem

        }
    }

}