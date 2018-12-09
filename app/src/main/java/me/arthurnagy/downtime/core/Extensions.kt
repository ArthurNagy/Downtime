package me.arthurnagy.downtime.core

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import me.arthurnagy.downtime.R
import me.arthurnagy.downtime.feature.shared.StringProvider
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

val UsageStats.appLaunchCount: Int
    get() = try {
        val appLaunchCountField = this::class.java.getDeclaredField("mAppLaunchCount")
        appLaunchCountField.getInt(this)
    } catch (exception: Exception) {
        0
    }

val UsageEvents.events: Iterable<UsageEvents.Event>
    get() = object : Iterable<UsageEvents.Event> {
        override fun iterator(): Iterator<UsageEvents.Event> = object : Iterator<UsageEvents.Event> {
            override fun hasNext(): Boolean = this@events.hasNextEvent()

            override fun next(): UsageEvents.Event {
                val nextEvent = UsageEvents.Event()
                this@events.getNextEvent(nextEvent)
                return nextEvent
            }

        }
    }

fun LocalDateTime.toUtcMillis() = this.toInstant(ZoneOffset.UTC).toEpochMilli()

fun Long.formatToDuration(stringProvider: StringProvider): String {
    val duration = Duration.ofMillis(this)
    val formatter: DateTimeFormatter = if (duration.toHours() >= 1) {
        DateTimeFormatter.ofPattern(stringProvider.getString(R.string.pattern_hours))
    } else {
        DateTimeFormatter.ofPattern(stringProvider.getString(R.string.pattern_minutes))
    }
    return when {
        duration.toMinutes() > 1 -> {
            val appScreenDuration = LocalTime.MIDNIGHT.plus(duration).atOffset(ZoneOffset.UTC)
            formatter.format(appScreenDuration)
        }
        else -> stringProvider.getString(R.string.less_then_one_minute)
    }
}