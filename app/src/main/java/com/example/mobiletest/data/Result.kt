package com.example.mobiletest.data

import java.lang.Exception

sealed class Result<out R> {
    data class Success<out T>(val data: T?): Result<T>()
    data class Error(val data: Exception?): Result<Nothing>()
}