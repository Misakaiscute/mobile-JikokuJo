package com.jikokujo.core.data

import com.google.gson.annotations.SerializedName

interface Payload
data class ResponseRoot<T: Payload>(
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("errors")
    val errors: List<String> = listOf()
)
