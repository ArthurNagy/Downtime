package me.arthurnagy.downtime.feature.overview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.core.AppUsage
import kotlin.math.*


class OverviewChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : PieChart(context, attrs, defStyle) {

    private var totalAppScreenTime = 0L

    init {
        renderer = OverviewChartRenderer(this, mAnimator, mViewPortHandler)
        isRotationEnabled = false
        setNoDataText("")
        holeRadius = 95F
        setDrawSlicesUnderHole(false)
        setHoleColor(ContextCompat.getColor(context, R.color.primary))
        legend.isEnabled = false
        description.text = ""
        setUsePercentValues(false)
        setExtraOffsets(40F, 0F, 40F, 0F)
        setEntryLabelTextSize(14F)
    }

    fun submitData(appUsageEntries: List<AppUsage>) {
        GlobalScope.launch(Dispatchers.Main) {
            totalAppScreenTime = appUsageEntries.sumBy { it.screenTime.toInt() }.toLong()
            centerText = totalAppScreenTime.toString()
            val pieAppData = withContext(Dispatchers.Default) {
                val pieEntries: List<PieEntry> = appUsageEntries.map(::transformAppEntryToPieEntry)
                val sortedEntries = pieEntries.sortedByDescending { it.value }
                val entriesToShow = sortedEntries.filter { it.value > FIVE_PERCENT }
                val entriesToHide = sortedEntries.filter { it.value <= FIVE_PERCENT }
                var otherPercentage = entriesToHide.sumBy { it.value.toInt() }.toFloat()

                val finalEntries: List<PieEntry> = if (entriesToShow.size > MAX_CHART_ENTRIES) {
                    otherPercentage += entriesToShow.subList(MAX_CHART_ENTRIES - 1, entriesToShow.size - 1).sumBy { it.value.toInt() }.toFloat()
                    entriesToShow.subList(0, MAX_CHART_ENTRIES - 1)
                } else {
                    entriesToShow
                }.toMutableList().apply { add(PieEntry(otherPercentage, context.getString(R.string.other))) }
                PieDataSet(finalEntries, "")
                    .apply {
                        colors = MATERIAL_COLORS.toList()
                        sliceSpace = 1F
                    }
            }
            mSelectionListener
            data = PieData(pieAppData)
            invalidate()
        }
    }

    private fun transformAppEntryToPieEntry(appEntry: AppUsage): PieEntry {
        val appScreenTimePercentage = calculateAppPercentage(appEntry.screenTime)
        return PieEntry(appScreenTimePercentage, appEntry.name, appEntry)
    }

    private fun calculateAppPercentage(appScreenTime: Long) = appScreenTime * 100F / totalAppScreenTime

    private class OverviewChartRenderer(pieChart: PieChart, chartAnimator: ChartAnimator, viewPortHandler: ViewPortHandler) :
        PieChartRenderer(pieChart, chartAnimator, viewPortHandler) {

        override fun drawValue(c: Canvas?, formatter: IValueFormatter?, value: Float, entry: Entry?, dataSetIndex: Int, x: Float, y: Float, color: Int) = Unit

        override fun drawEntryLabel(c: Canvas, label: String, x: Float, y: Float) {
            val textBounds = Rect()
            paintEntryLabels.getTextBounds(label, 0, label.length, textBounds)
            val center = mChart.centerOffsets
            val entryAngleRadian = mChart.getAngleForPoint(x, y) * PI / 180F

            val circleX = center.x + mChart.radius * cos(entryAngleRadian)
            val circleY = center.y + mChart.radius * sin(entryAngleRadian)

            val distance = sqrt((x - circleX).pow(2) + (y - circleY).pow(2))

            val newX = center.x + (mChart.radius + textBounds.width() / 1.75 + distance) * cos(entryAngleRadian)
            val newY = center.y + (mChart.radius + textBounds.height() * 1.25 + distance) * sin(entryAngleRadian)
            super.drawEntryLabel(c, label, newX.toFloat(), newY.toFloat())
        }

    }

    companion object {
        private const val FIVE_PERCENT = 5F
        private const val MAX_CHART_ENTRIES = 10
        private val MATERIAL_COLORS = intArrayOf(
            Color.parseColor("#2196F3"), Color.parseColor("#F44336"), Color.parseColor("#FFC107"),
            Color.parseColor("#4CAF50"), Color.parseColor("#9C27B0"), Color.parseColor("#009688"),
            Color.parseColor("#FF5722"), Color.parseColor("#607D8B"), Color.parseColor("#673AB7"),
            Color.parseColor("#E91E63")
        )
    }

}