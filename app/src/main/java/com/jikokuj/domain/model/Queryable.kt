package com.jikokuj.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface Queryable {
    @Serializable
    data class Route(
        @SerialName("id") val id: String,
        @SerialName("name") val name: String,
    ): Queryable
    @Serializable
    data class Stop(
        @SerialName("id") val id: String,
        @SerialName("name") val name: String,
    ): Queryable
}

@Serializable
data class RouteDetailed(
    val route: Queryable.Route,
    val location: List<Location>,
)