package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName

data class Trip(
    @SerializedName("id") val id: String,
    @SerializedName("short_name") val shortName: String,
    @SerializedName("headsign") val headsign: String,
    @SerializedName("shape_id") val shape: String,
    @SerializedName("stops") val stops: List<Stop>,
    @SerializedName("wheelchair_accessible") val wheelchairAccessible: Int,
    @SerializedName("bikes_allowed") val bikesAllowed: Int
)