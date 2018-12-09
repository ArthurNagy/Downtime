package me.arthurnagy.downtime.core

import android.graphics.drawable.Drawable

data class AppUsage(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val screenTime: Long,
    val notificationsReceived: Int,
    val timesOpened: Int
)

