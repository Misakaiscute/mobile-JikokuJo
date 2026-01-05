package com.jikokujo.schedule.data.remote

import com.google.gson.annotations.SerializedName
import com.jikokujo.core.data.Payload
import com.jikokujo.schedule.data.model.Queryable

data class GetQueryablesResponseStructure(
    @SerializedName("stops")
    val stops: List<Queryable.Stop> = listOf(),
    @SerializedName("routes")
    val routes: List<Queryable.Route> = listOf()
): Payload