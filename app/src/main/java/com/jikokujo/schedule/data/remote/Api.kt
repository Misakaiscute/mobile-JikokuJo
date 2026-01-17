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
    @GET("route/{routeId}/time/{year}{month}{day}/{hour}{minute}")
    suspend fun getTripsFromRoute(
        @Path("selectedId") routeId: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
        @Path("hour") hour: String,
        @Path("minute") minute: String,
    ): ResponseRoot<GetTripsObj>

    @Headers("accept: application/json")
    @GET("stop/{stopId}/time/{year}{month}{day}/{hour}{minute}")
    suspend fun getTripsFromStop(
        @Path("stopId") stopId: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
        @Path("hour") hour: String,
        @Path("minute") minute: String,
    ): ResponseRoot<GetTripsObj>

    @Headers("accept: application/json")
    @GET("route/{tripId}/possible-shapes")
    suspend fun getShapesForTrip(
        @Path("tripId") tripId: String
    ): ResponseRoot<GetShapeForTripObj>

    @Headers("accept: application/json")
    @GET("route/{tripId}/possible-shapes")
    suspend fun getStopsForTrip(
        @Path("tripId") tripId: String
    ): ResponseRoot<GetStopsForTripObj>
}
