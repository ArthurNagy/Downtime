package me.arthurnagy.downtime.core

data class AppUsage(val name: String, val packageName: String, val screenTime: Long, val notificationsReceived: Int, val timesOpened: Int)