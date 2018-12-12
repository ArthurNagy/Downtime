package me.arthurnagy.downtime.feature.dashboard.usage.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.feature.shared.UsageType

class UsageSpinnerAdapter(context: Context) : ArrayAdapter<UsageUiModel>(context, R.layout.item_usage) {

    private val items: List<UsageUiModel>

    init {
        val usageTypeStrings = context.resources.getStringArray(R.array.usage_types)
        items = UsageType.values().mapIndexed { index, usageType -> UsageUiModel(usageType, usageTypeStrings[index]) }
        addAll(items)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        getUsageTypeItemView(position, convertView, parent, R.layout.item_usage_selected)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        getUsageTypeItemView(position, convertView, parent, R.layout.item_usage)

    fun getPositionForUsageType(usageType: UsageType) = items.indexOfFirst { it.usageType == usageType }

    fun getUsageType(position: Int): UsageType = getItem(position)!!.usageType

    private fun getUsageTypeItemView(position: Int, convertView: View?, parent: ViewGroup, @LayoutRes layoutResource: Int): View {
        val newConvertView: View

        val viewHolder: UsageTypeItemViewHolder
        if (convertView == null) {
            val inflatedView = LayoutInflater.from(context).inflate(layoutResource, parent, false)
            viewHolder = UsageTypeItemViewHolder(inflatedView.findViewById(R.id.label))
            newConvertView = inflatedView
            newConvertView.tag = viewHolder
        } else {
            newConvertView = convertView
            viewHolder = convertView.tag as UsageTypeItemViewHolder
        }

        viewHolder.bind(getItem(position))
        return newConvertView
    }

    private class UsageTypeItemViewHolder(val usageTypeLabel: TextView) {
        fun bind(usageTypeUiModel: UsageUiModel?) {
            usageTypeLabel.text = usageTypeUiModel?.usageTypeLabel
        }
    }

}