package me.arthurnagy.downtime.feature.overview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.listener.PieRadarChartTouchListener
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.coroutines.*
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.core.AppUsage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

typealias OverviewAppSelectionListener = (selectedApp: AppUsage?) -> Unit

class OverviewChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    PieChart(context, attrs, defStyle), CoroutineScope {

    private var job = Job()
    private var totalAppScreenTime = 0L
    private val appPrimaryColor = ContextCompat.getColor(context, R.color.primary)
    private var appSelectionListener: OverviewAppSelectionListener? = null

    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    init {
        renderer = OverviewChartRenderer(this, mAnimator, mViewPortHandler)
        onTouchListener = OverviewChartTouchListener(this)

        setUsePercentValues(false)
        description.isEnabled = false
        setExtraOffsets(40F, 0F, 40F, 0F)

        isDrawHoleEnabled = true
        setHoleColor(appPrimaryColor)
        setDrawSlicesUnderHole(true)

        holeRadius = 95f

        setDrawCenterText(true)

        isRotationEnabled = false
        isHighlightPerTapEnabled = true

        setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                appSelectionListener?.invoke(null)
            }

            override fun onValueSelected(e: Entry, h: Highlight) {
                val appUsage: AppUsage? = e.data as? AppUsage
                appSelectionListener?.invoke(appUsage)
            }
        })

        legend.isEnabled = false

        setNoDataText("")
        description.isEnabled = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        job = Job()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
    }

    fun submitData(appUsageEntries: List<AppUsage>) {
        launch {
            totalAppScreenTime = appUsageEntries.sumBy { it.screenTime.toInt() }.toLong()
            val timeFormatter = SimpleDateFormat("H 'hr' mm 'min'", Locale.getDefault())
            val todaySpentTime = Calendar.getInstance().apply { timeInMillis = totalAppScreenTime }
            centerText = buildSpannedString {
                inSpans(TextAppearanceSpan(context, R.style.TextAppearance_MaterialComponents_Headline5)) {
                    append(timeFormatter.format(todaySpentTime.timeInMillis))
                }
                append("\n")
                inSpans(TextAppearanceSpan(context, R.style.TextAppearance_MaterialComponents_Body1)) {
                    append(context.getString(R.string.today))
                }
            }
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
                        selectionShift = 1F
                        xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                        valueLineColor = Color.TRANSPARENT
                        valueLinePart1Length = 0.1F
                        valueLinePart2Length = 0.1F
                    }
            }
            data = PieData(pieAppData)
            highlightValue(null)
            invalidate()
        }
    }

    fun setAppSelectionListener(appSelectionListener: OverviewAppSelectionListener) {
        this.appSelectionListener = appSelectionListener
    }

    private fun transformAppEntryToPieEntry(appEntry: AppUsage): PieEntry {
        val appScreenTimePercentage = calculateAppPercentage(appEntry.screenTime)
        return PieEntry(appScreenTimePercentage, appEntry.name, appEntry)
    }

    private fun calculateAppPercentage(appScreenTime: Long) = appScreenTime * 100F / totalAppScreenTime

    private class OverviewChartRenderer(pieChart: PieChart, chartAnimator: ChartAnimator, viewPortHandler: ViewPortHandler) :
        PieChartRenderer(pieChart, chartAnimator, viewPortHandler) {

        override fun drawValue(c: Canvas?, formatter: IValueFormatter?, value: Float, entry: Entry?, dataSetIndex: Int, x: Float, y: Float, color: Int) = Unit
    }

    private class OverviewChartTouchListener(private val chart: PieChart) : PieRadarChartTouchListener(chart) {

        private val chartDistanceThreshold = chart.context.resources.getDimensionPixelSize(R.dimen.chart_offset)

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val tapDistance = chart.distanceToCenter(e.x, e.y)
            val holeDistance = chart.radius * (chart.holeRadius - 25F) / 100F
            return when {
                tapDistance < holeDistance -> {
                    chart.highlightValue(null, true)
                    mLastGesture = null
                    true
                }
                tapDistance > chart.radius && tapDistance <= chart.radius + chartDistanceThreshold -> {
                    val angle = chart.getAngleForPoint(e.x, e.y) / chart.animator.phaseY
                    val index = chart.getIndexForAngle(angle)
                    // check if the index could be found
                    return if (index < 0 || index >= chart.data.maxEntryCountSet.entryCount) {
                        super.onSingleTapUp(e)
                    } else {
                        val set = chart.data.dataSet
                        val entry = set.getEntryForIndex(index)
                        val highlight = Highlight(index.toFloat(), entry.y, e.x, e.y, 0, set.axisDependency)
                        chart.highlightValue(highlight, true)
                        mLastGesture = ChartGesture.SINGLE_TAP
                        true
                    }
                }
                else -> super.onSingleTapUp(e)
            }
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