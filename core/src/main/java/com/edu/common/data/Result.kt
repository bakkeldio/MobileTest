package com.edu.common.data

sealed class Result<out R> {
    data class Success<out T>(val data: T?) : Result<T>()
    data class Error(val data: Throwable?) : Result<Nothing>()
}