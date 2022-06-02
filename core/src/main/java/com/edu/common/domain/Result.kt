package com.edu.common.domain

sealed class Result<out T> {
    data class Success<out T>(val data: T?) : Result<T>()
    data class Error(val data: Throwable?) : Result<Nothing>()


    suspend fun <R> processResult(
        onSuccess: suspend (T) -> Result<R>,
        onError: ((String) -> Unit)? = null
    ): Result<R> {
        return try {
            when (this) {
                is Success -> onSuccess(
                    this.data ?: throw IllegalArgumentException("model can't be null")
                )
                is Error -> {
                    onError?.invoke(this.data?.localizedMessage ?: "")
                    this
                }
            }
        } catch (e: Exception) {
            Error(e)
        }
    }
}