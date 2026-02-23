package com.jikokujo.core.data.repository

import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.model.User

interface UserRepository {
    var loggedInUser: ApiResult<User>?
    var userAccessToken: String?
    companion object {
        fun String.toBearer() = "Bearer $this"
    }
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
    )
    suspend fun logout()
    suspend fun checkAuth()
    suspend fun getLoggedInUser()
}