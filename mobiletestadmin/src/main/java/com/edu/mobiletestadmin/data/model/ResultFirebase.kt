package com.edu.mobiletestadmin.data.model

sealed class ResultFirebase<out T> {
    data class Success<out T>(val data: T) : ResultFirebase<T>()
    data class Error(val error: Throwable) : ResultFirebase<Nothing>()
}