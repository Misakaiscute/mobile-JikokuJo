package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName

sealed interface Queryable {
    data class Route(
        @SerializedName("route_id")
        val id: String,
        @SerializedName("route_short_name")
        val name: String,
        @SerializedName("type")
        val type: String //TODO: THIS WILL BE INT
    ): Queryable
    data class Stop(
        @SerializedName("ids")
        val idsAssociated: List<String> = listOf(),
        @SerializedName("name")
        val name: String,
    ): Queryable
}

data class RouteDetailed(
    val route: Queryable.Route,
    val location: List<Location>,
)