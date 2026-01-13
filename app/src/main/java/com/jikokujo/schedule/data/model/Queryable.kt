package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName
import com.jikokujo.R

sealed interface Queryable {
    data class Route(
        @SerializedName("route_id")
        val id: String,
        @SerializedName("route_short_name")
        val name: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("color")
        val color: String
    ): Queryable
    data class Stop(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
    ): Queryable
}
fun Queryable.Route.getIconForType() = when(type){
    1 -> R.drawable.bus
    2 -> R.drawable.tram
    3 -> R.drawable.subway
    4 -> R.drawable.trolleybus
    5 -> R.drawable.train
    6 -> R.drawable.train
    7 -> R.drawable.other_transport_types
    8 -> R.drawable.other_transport_types
    else -> throw Exception("Invalid type! Type must be between 1 and 8!")
}