package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName

sealed interface Location {
    data class Anonymous(
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double
    ): Location
    data class Endpoint(
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double
    ): Location
    data class Stop(
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double
    ): Location
}