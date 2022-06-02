package com.edu.common.presentation

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    protected val _loading: MutableLiveData<Boolean> = MutableLiveData(false)

    val loading: LiveData<Boolean> get() = _loading

    protected val _error: SingleLiveEvent<Throwable> = SingleLiveEvent()

    protected companion object{
        const val UPLOAD_WORK = "UPLOAD_WORK"
        const val TIMER_WORK = "TIMER_WORK"
    }

    val error: LiveData<String>
        get() = Transformations.map(_error) {
            it.localizedMessage
        }

    protected fun showLoader() {
        _loading.value = true
    }

    protected fun hideLoader() {
        _loading.value = false
    }

}