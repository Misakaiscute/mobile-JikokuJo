package com.jikokujo.schedule.data.remote

import com.jikokujo.core.data.remote.ResponseRoot
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface QueryablesApi {
    @Headers("accept: application/json")
    @GET("queryables")
    suspend fun getQueryables(): ResponseRoot<GetQueryablesObj>

    @FormUrlEncoded
    @Headers("accept: application/json")
    @POST("route/trip")
    suspend fun getTripsFromRoute(
        @Field("route_id") routeId: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): ResponseRoot<GetTripsObj>

    @FormUrlEncoded
    @Headers("accept: application/json")
    @POST("stop/trip")
    suspend fun getTripsFromStop(
        @Field("ids") stopIds: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): ResponseRoot<GetTripsObj>

    @FormUrlEncoded
    @Headers("accept: application/json")
    @POST("trip/shapes")
    suspend fun getShapesForTrip(
        @Field("trip_id") tripId: String
    ): ResponseRoot<GetShapesForTripObj>

    @FormUrlEncoded
    @Headers("accept: application/json")
    @POST("trip/stops")
    suspend fun getStopsForTrip(
        @Field("trip_id") tripId: String
    ): ResponseRoot<GetStopsForTripObj>
}
