package com.jikokujo.profile.data.remote

import com.google.gson.annotations.SerializedName
import com.jikokujo.core.data.Payload

data class UserLoginObj(
    @SerializedName("token")
    val userAccessToken: String
): Payload