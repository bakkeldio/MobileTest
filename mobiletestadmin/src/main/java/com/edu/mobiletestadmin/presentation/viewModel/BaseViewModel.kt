package com.edu.mobiletestadmin.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.edu.mobiletestadmin.presentation.model.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseViewModel : ViewModel() {


    protected val _loading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val loading: LiveData<Boolean> = _loading
    protected val _error: SingleLiveEvent<Throwable> = SingleLiveEvent()
    val error: LiveData<String> = Transformations.map(_error) {
        it.localizedMessage
    }


    protected inline fun <T> toResourceState(block: () -> ResultFirebase<T>): ResourceState<T> {
        return when (val response = block.invoke()) {
            is ResultFirebase.Success -> ResourceState.Success(response.data)
            is ResultFirebase.Error -> ResourceState.Error(response.error.localizedMessage)
        }
    }

    protected inline fun <T> getResourceStateFlow(crossinline block: suspend () -> Flow<ResultFirebase<T>>) =
        flow {
            block.invoke().collect {
                emit(toResourceState {
                    it
                })
            }
        }


}