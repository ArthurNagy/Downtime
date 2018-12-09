package me.arthurnagy.downtime.feature.shared

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(private val context: Context) {

    fun getString(@StringRes stringResource: Int) = context.getString(stringResource)

    fun getString(@StringRes stringResource: Int, vararg arguments: Any) = context.getString(stringResource, arguments)

}