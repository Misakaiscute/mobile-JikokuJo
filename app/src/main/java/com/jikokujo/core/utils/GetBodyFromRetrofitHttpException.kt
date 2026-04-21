package com.jikokujo.core.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jikokujo.core.data.remote.Payload
import com.jikokujo.core.data.remote.ResponseRoot
import retrofit2.HttpException

inline fun <reified T : Payload> HttpException.errorAs(): ResponseRoot<T> {
    val errorBody = response()?.errorBody()?.string()
    val type = TypeToken.getParameterized(ResponseRoot::class.java, T::class.java).type
    return Gson().fromJson(errorBody, type)
}