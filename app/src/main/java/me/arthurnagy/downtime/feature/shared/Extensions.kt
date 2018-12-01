package me.arthurnagy.downtime.feature.shared

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

inline fun consumeOptionsItemSelected(block: () -> Unit): Boolean {
    block()
    return true
}

fun Fragment.requireAppCompatActivity() = this.requireActivity() as AppCompatActivity

inline fun <reified VB : ViewDataBinding> Fragment.binding(@LayoutRes layoutRes: Int): Lazy<VB> = lazy {
    DataBindingUtil.inflate<VB>(this.layoutInflater, layoutRes, null, false)
}