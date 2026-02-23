package com.jikokujo.core.data.remote

sealed interface ApiResult<out T>{
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val errorMsg: String) : ApiResult<Nothing>
}