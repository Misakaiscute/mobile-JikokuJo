package com.jikokujo.core.data.repository

import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import kotlinx.coroutines.flow.MutableStateFlow

interface UserRepository {
    val favourites: MutableStateFlow<List<Favourite>?>
    var userAccessToken: String?
    companion object {
        fun String.toBearer() = "Bearer $this"
    }
    suspend fun check(): Boolean
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        passwordConfirmation: String,
    ): ApiResult<Nothing?>
    suspend fun login(
        email: String,
        password: String,
        remember: Boolean
    ): ApiResult<Nothing?>
    suspend fun logout(): ApiResult<Nothing?>
    suspend fun getUser(): ApiResult<User>
    suspend fun getFavourites(): ApiResult<List<Favourite>>
    suspend fun toggleFavourite(
        routeId: String,
        time: Int
    ): ApiResult<Queryable.Route?>
}