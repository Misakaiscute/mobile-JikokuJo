package com.jikokujo.schedule.data.remote

import com.jikokujo.core.data.ResponseRoot
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface QueryablesApi {
    @Headers("accept: application/json")
    @GET("queryables")
    suspend fun getQueryables(): ResponseRoot<GetQueryablesObj>

    @Headers("accept: application/json")
    @GET("route/{routeId}/time/{year}{month}{day}/{hour}{minute}")
    suspend fun getTripsFromRoute(
        @Path("routeId") routeId: String,
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
    @GET("trip/{tripId}/shapes")
    suspend fun getShapesForTrip(
        @Path("tripId") tripId: String
    ): ResponseRoot<GetShapesForTripObj>

    @Headers("accept: application/json")
    @GET("trip/{tripId}/stops")
    suspend fun getStopsForTrip(
        @Path("tripId") tripId: String
    ): ResponseRoot<GetStopsForTripObj>
}
