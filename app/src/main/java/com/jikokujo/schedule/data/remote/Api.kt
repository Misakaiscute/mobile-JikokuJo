package com.jikokujo.schedule.data.remote

import com.jikokujo.core.data.ResponseRoot
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
    suspend fun getQueryables(): ResponseRoot<GetQueryablesObj>

    @Headers("accept: application/json")
    @GET("stop/{selectedId}/routes")
    suspend fun getRoutesFromStop(
        @Path("selectedId") selectedStop: String
    ): ResponseRoot<GetRoutesFromStopObj>

    @Headers("accept: application/json")
    @GET("route/{selectedId}/possible-shapes")
    suspend fun getPossibleShapesForRoute(
        @Path("selectedId") selectedRoute: String
    ): ResponseRoot<GetPossibleShapesForRouteObj>

    @Headers("accept: application/json")
    @GET("route/{selectedId}/from/{year}/{month}/{day}/{hour}/{minute}")
    suspend fun getTrips(
        @Path("selectedId") selectedRoute: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int,
        @Path("hour") hour: Int,
        @Path("minute") minute: Int,
    ): ResponseRoot<GetTripsObj>
}