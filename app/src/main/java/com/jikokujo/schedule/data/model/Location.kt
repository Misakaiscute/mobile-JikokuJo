package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName

sealed interface Location {
    data class Auxiliary(
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double
    ): Location
    data class RoutePathPoint(
        @SerializedName("id") val id: String,
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double
    ): Location
    data class Stop(
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double
    ): Location
}