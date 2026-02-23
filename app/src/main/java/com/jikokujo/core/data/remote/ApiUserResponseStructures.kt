package com.jikokujo.core.data.remote

import com.google.gson.annotations.SerializedName
import com.jikokujo.core.data.model.User

data class UserLoginObj(
    @SerializedName("token")
    val userAccessToken: String
) : Payload

data class GetUserObj(
    @SerializedName("user")
    val user: User
) : Payload