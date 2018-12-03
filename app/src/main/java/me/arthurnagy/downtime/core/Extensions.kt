package me.arthurnagy.downtime.core

import android.app.usage.UsageEvents
import android.app.usage.UsageStats

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