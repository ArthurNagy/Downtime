package me.arthurnagy.downtime.feature.shared

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class UsageType : Parcelable {
    SOT,
    UNLOCKS,
    NOTIFICATIONS
}