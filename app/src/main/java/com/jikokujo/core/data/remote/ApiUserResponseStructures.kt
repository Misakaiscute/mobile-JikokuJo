package com.jikokujo.core.data.remote

import com.google.gson.annotations.SerializedName
import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.schedule.data.model.Queryable

data class UserLoginObj(
    @SerializedName("token")
    val userAccessToken: String
) : Payload

data class GetUserObj(
    @SerializedName("user")
    val user: User
) : Payload

data class ToggleFavouriteObj(
    @SerializedName("route")
    val route: Queryable.Route,
    @SerializedName("new_status")
    val isCreated: Boolean
) : Payload

data class GetFavouritesObj(
    @SerializedName("favourites")
    val favourites: List<Favourite>
) : Payload