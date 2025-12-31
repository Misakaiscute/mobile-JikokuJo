package com.jikokuj.data.remote

import com.jikokuj.domain.model.Queryable
import com.jikokuj.domain.model.RouteDetailed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

sealed interface ApiResult<out T>{
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val errorMsg: String) : ApiResult<Nothing>
}

interface Api {
    @Headers("Accept: application/json")
    @GET("queryables")
    fun getQueryables(): Call<List<Queryable>>

    @Headers("Accept: application/json")
    @GET("routes/{selectedId}/details") //TODO: double check endpoint name
    fun getRouteDetailsFromStop(@Path("selectedId") selectedId: String): Call<List<RouteDetailed>>

    @Headers("Accept: application/json")
    @GET("routes/{selectedId}/details") //TODO: double check endpoint name
    fun getRouteDetailsFromRoute(@Path("selectedId") selectedId: String): Call<List<RouteDetailed>>
}