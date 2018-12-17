package me.arthurnagy.downtime.feature.dashboard.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.arthurnagy.downtime.AppUsageBinding
import me.arthurnagy.downtime.core.AppUsage
import me.arthurnagy.downtime.feature.shared.ItemClickListener
import me.arthurnagy.downtime.feature.shared.StringProvider
import me.arthurnagy.downtime.feature.shared.UsageType

class AppUsageAdapter(private val stringProvider: StringProvider) : RecyclerView.Adapter<AppUsageAdapter.AppUsageViewHolder>() {

    private val appUsageListUpdateCallback = AppUsageListUpdateCallback(this)
    private val asyncListDiffer =
        AsyncListDiffer<AppUiModel>(appUsageListUpdateCallback, AsyncDifferConfig.Builder<AppUiModel>(diffItemCallback).build()).apply { submitList(listOf()) }
    private var itemClickListener: ItemClickListener? = null

    private var usageType: UsageType = UsageType.SOT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppUsageViewHolder = AppUsageViewHolder.create(parent, itemClickListener)

    override fun onBindViewHolder(holder: AppUsageViewHolder, position: Int) {
        holder.binding.app = asyncListDiffer.currentList[position]
        holder.binding.executePendingBindings()
    }

    override fun onBindViewHolder(holder: AppUsageViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads.contains(UsageLabelPayload)) {
            holder.binding.usageValue.text = asyncListDiffer.currentList[position].usageLabel
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    fun submitData(usageType: UsageType, apps: List<AppUsage>) {
        this.usageType = usageType
        asyncListDiffer.submitList(apps.map { AppUiModel(usageType, it, stringProvider) })
    }

    fun getItem(position: Int): AppUsage = asyncListDiffer.currentList[position].appUsage

    fun setOnUpdateFinishedCallback(updateFinishedCallback: OnUpdateFinished) = appUsageListUpdateCallback.setUpdateFinishedCallback(updateFinishedCallback)

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    class AppUsageViewHolder(val binding: AppUsageBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, itemClickListener: ItemClickListener?) =
                AppUsageViewHolder(AppUsageBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
                    binding.root.setOnClickListener {
                        itemClickListener?.invoke(this.adapterPosition)
                    }
                }
        }

    }

    companion object {

        private object UsageLabelPayload

        private val diffItemCallback = object : DiffUtil.ItemCallback<AppUiModel>() {
            override fun areItemsTheSame(oldItem: AppUiModel, newItem: AppUiModel): Boolean = oldItem.appUsage.packageName == newItem.appUsage.packageName

            override fun areContentsTheSame(oldItem: AppUiModel, newItem: AppUiModel): Boolean = oldItem == newItem

            override fun getChangePayload(oldItem: AppUiModel, newItem: AppUiModel): Any? = if (newItem.usageLabel != oldItem.usageLabel) {
                UsageLabelPayload
            } else null
        }
    }

}