package com.jikokujo.schedule.data.model

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName

data class StopWithLocationAndStopTime(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: Location.Stop,
    @SerializedName("arrival_time")
    val arrivalTime: Int
)

@SuppressLint("DefaultLocale")
fun StopWithLocationAndStopTime.arrivalTimeFormatted(): String{
    val hours = (arrivalTime / 60)
    val minutes = (arrivalTime % 60)
    return String.format("%02d", hours) + ':' + String.format("%02d", minutes)
}