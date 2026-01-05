package com.jikokujo.schedule.data.remote

import com.jikokujo.core.data.ResponseRoot
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

sealed interface ApiResult<out T>{
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val errorMsg: String) : ApiResult<Nothing>
}

interface Api {
    @Headers("accept: application/json")
    @GET("queryables")
    suspend fun getQueryables(): ResponseRoot<GetQueryablesResponseStructure>

    @Headers("accept: application/json")
    @GET("routes/{selectedId}/details") //TODO: double check endpoint name
    suspend fun getRouteDetailsFromStop(@Path("selectedId") selectedId: String): Call<ResponseRoot<GetQueryablesResponseStructure>>

    @Headers("accept: application/json")
    @GET("routes/{selectedId}/details") //TODO: double check endpoint name
    suspend fun getRouteDetailsFromRoute(@Path("selectedId") selectedId: String): Call<ResponseRoot<GetQueryablesResponseStructure>>
}