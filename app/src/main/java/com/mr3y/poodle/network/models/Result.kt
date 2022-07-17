package com.mr3y.poodle.network.models

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable? = null) : Result<Nothing>
    object Loading : Result<Nothing>
}

/**
 * [Success.data] if [Result] is of type [Success]
 */
fun <R> Result<R>.successOr(fallback: R): R {
    return (this as? Result.Success)?.data ?: fallback
}

val <R> Result<R>.data: R?
    get() = (this as? Result.Success)?.data
