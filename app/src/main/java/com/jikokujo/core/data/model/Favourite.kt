package com.jikokujo.core.data.model

import com.google.gson.annotations.SerializedName
import com.jikokujo.schedule.data.model.Queryable

data class Favourite(
    @SerializedName("time")
    val atMins: Int,
    @SerializedName("route")
    val route: Queryable.Route
)
