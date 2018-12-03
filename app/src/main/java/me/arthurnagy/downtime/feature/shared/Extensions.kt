package me.arthurnagy.downtime.feature.shared

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

val Any?.exhaustive get() = Unit

inline fun consumeOptionsItemSelected(block: () -> Unit): Boolean {
    block()
    return true
}

fun Fragment.requireAppCompatActivity() = this.requireActivity() as AppCompatActivity

inline fun <reified VB : ViewDataBinding> Fragment.binding(@LayoutRes layoutRes: Int): Lazy<VB> = lazy {
    DataBindingUtil.inflate<VB>(this.layoutInflater, layoutRes, null, false)
}

inline fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    this.observe(lifecycleOwner, androidx.lifecycle.Observer { data ->
        observer(data)
    })
}

inline fun <T> LiveData<T>.observeNonNull(lifecycleOwner: LifecycleOwner, crossinline observer: (T) -> Unit) {
    this.observe(lifecycleOwner, androidx.lifecycle.Observer { data ->
        data?.let { observer(it) }
    })
}

fun <T> mutableLiveDataOf(initialValue: T?) = MutableLiveData<T>().apply { value = initialValue }