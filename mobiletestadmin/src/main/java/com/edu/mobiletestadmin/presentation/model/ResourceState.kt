package com.edu.mobiletestadmin.presentation.model

sealed class ResourceState<out R> {
    data class Success<out T>(val data: T) : ResourceState<T>()
    data class Error(val error: String?) : ResourceState<Nothing>()
    object Loading : ResourceState<Nothing>()
    object Empty: ResourceState<Nothing>()
}