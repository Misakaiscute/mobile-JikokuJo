package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName

data class StopWithLocationAndStopTime(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: Location.Stop,
    @SerializedName("arrival_time")
    val arrivalTime: Int,
    @SerializedName("stop_sequence")
    val order: Int
)