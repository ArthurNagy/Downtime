package me.arthurnagy.downtime.feature.shared

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(private val context: Context) {

    fun getString(@StringRes stringResource: Int): String = context.getString(stringResource)

    fun getString(@StringRes stringResource: Int, vararg arguments: Any): String = context.getString(stringResource, *arguments)

}