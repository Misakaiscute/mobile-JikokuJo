package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName

sealed interface Location {
    data class Auxiliary(
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double
    ): Location
    data class Stop(
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double
    ): Location
}
data class RoutePathPoint(
    @SerializedName("sequence") val order: Int,
    @SerializedName("location") val location: Location.Auxiliary
)