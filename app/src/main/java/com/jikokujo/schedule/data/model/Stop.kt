package com.jikokujo.schedule.data.model

import com.google.gson.annotations.SerializedName

data class Stop(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("location")
    val location: Location.Stop? = null,
    @SerializedName("name")
    val name: String? = null
)