package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName

data class Trip(
    @SerializedName("id") val id: String,
    @SerializedName("headsign") val headSign: String,
    @SerializedName("route_id") val routeId: String,
    @SerializedName("shape_id") val shapeId: String,
    @SerializedName("stops") val stops: List<StopWithLocationAndStopTime>,
    @SerializedName("wheelchair_accessible") val wheelchairAccessible: Int,
    @SerializedName("bikes_allowed") val bikesAllowed: Int,
    @SerializedName("direction_id") val directionId: Int
)