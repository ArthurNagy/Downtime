package me.arthurnagy.downtime.feature.shared

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

val Any?.exhaustive get() = Unit

inline fun consumeOptionsItemSelected(block: () -> Unit): Boolean {
    block()
    return true
}

fun Fragment.requireAppCompatActivity() = this.requireActivity() as AppCompatActivity

//fun ImageView.loadDrawable()

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

inline fun <T> dependantLiveData(vararg dependencies: LiveData<out Any>, defaultValue: T? = null, crossinline mapper: () -> T?) =
    MediatorLiveData<T>().also { mediatorLiveData ->
        val observer = Observer<Any> { mediatorLiveData.value = mapper() }
        dependencies.forEach { dependencyLiveData ->
            mediatorLiveData.addSource(dependencyLiveData, observer)
        }
    }.apply { value = defaultValue }

inline fun <T> dependantObservableField(vararg dependencies: Observable, defaultValue: T? = null, crossinline mapper: () -> T?) =
    object : ObservableField<T>(*dependencies) {
        override fun get(): T? {
            return mapper()
        }
    }.apply { set(defaultValue) }
