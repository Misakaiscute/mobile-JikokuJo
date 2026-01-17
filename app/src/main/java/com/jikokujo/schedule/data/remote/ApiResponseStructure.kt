package com.jikokujo.schedule.data.remote

import com.google.gson.annotations.SerializedName
import com.jikokujo.core.data.Payload
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip

data class GetQueryablesObj(
    @SerializedName("stops")
    val stops: List<Queryable.Stop>,
    @SerializedName("routes")
    val routes: List<Queryable.Route>
): Payload
data class GetTripsObj(
    @SerializedName("trips")
    val trips: List<Trip>
): Payload
data class GetShapeForTripObj(
    @SerializedName("shapes")
    val shapes: List<Location.RoutePathPoint>
): Payload
data class GetStopsForTripObj(
    @SerializedName("stops")
    val stops: List<StopWithLocationAndStopTime>
): Payload
