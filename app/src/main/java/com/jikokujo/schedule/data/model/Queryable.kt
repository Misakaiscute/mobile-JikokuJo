package com.jikokujo.schedule.data.model

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import com.jikokujo.R

sealed interface Queryable {
    data class Route(
        @SerializedName("route_id") val id: String,
        @SerializedName("route_short_name") val shortName: String,
        @SerializedName("color") val color: String,
        @SerializedName("type") val type: Int
    ): Queryable
    data class Stop(
        @SerializedName("ids") val ids: List<String>,
        @SerializedName("name") val name: String,
    ): Queryable
}

@Throws(IllegalArgumentException::class)
fun Queryable.Route.getColor(hexOpacity: String = "FF"): Color{
    if (hexOpacity.length != 2 && !hexOpacity.contains(Regex("/[g-z][G-Z]"))){
        throw IllegalArgumentException("given opacity must be 2 characters long, and must be within 0-f/F")
    }
    return Color((hexOpacity + color).toLong(16))
}
fun Queryable.Route.getIcon() = when(type){
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