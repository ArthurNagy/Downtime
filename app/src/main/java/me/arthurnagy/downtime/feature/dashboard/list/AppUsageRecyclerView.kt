package me.arthurnagy.downtime.feature.dashboard.list

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.core.AppUsage
import me.arthurnagy.downtime.feature.shared.StringProvider
import me.arthurnagy.downtime.feature.shared.UsageType

class AppUsageRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    RecyclerView(context, attrs, defStyle) {

    private val adapter = AppUsageAdapter(StringProvider(context))

    init {
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            ContextCompat.getDrawable(context, R.drawable.list_divider)?.let { setDrawable(it) }
        })
        setAdapter(adapter)
//        adapter.setOnUpdateFinishedCallback {
//            smoothScrollToPosition(0)
//        }
    }

    fun submitAppList(usageType: UsageType, apps: List<AppUsage>) = adapter.submitData(usageType, apps)

    fun setItemClickListener(callback: (appUsage: AppUsage) -> Unit) {
        adapter.setItemClickListener {
            callback.invoke(adapter.getItem(it))
        }
    }
}