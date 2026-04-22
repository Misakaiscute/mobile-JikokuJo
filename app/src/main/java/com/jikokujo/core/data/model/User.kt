package com.jikokujo.core.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("second_name")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    val password: String? = null
)