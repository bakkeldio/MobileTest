package com.edu.common.presentation

sealed class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    object Empty: ResourceState<Nothing>()
    data class Success<T>(val data: T): ResourceState<T>()
    data class Error(val message: String): ResourceState<Nothing>()
}