package com.jikokuj.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface Location {
    @Serializable
    data class Anonymous(
        @SerialName("lon") val lon: Double,
        @SerialName("lat") val lat: Double
    ): Location
    @Serializable
    data class Endpoint(
        @SerialName("lon") val lon: Double,
        @SerialName("lat") val lat: Double
    ): Location
    @Serializable
    data class Stop(
        @SerialName("lon") val lon: Double,
        @SerialName("lat") val lat: Double
    ): Location
}