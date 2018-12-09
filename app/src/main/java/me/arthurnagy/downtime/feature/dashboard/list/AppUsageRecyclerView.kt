package me.arthurnagy.downtime.feature.dashboard.list

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.arthurnagy.downtime.core.AppUsage
import me.arthurnagy.downtime.feature.shared.UsageType

class AppUsageRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    RecyclerView(context, attrs, defStyle) {

    private val adapter = AppUsageAdapter()

    init {
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
//        addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
        setAdapter(adapter)
    }

    fun submitAppList(usageType: UsageType, apps: List<AppUsage>) = adapter.submitData(usageType, apps)

}