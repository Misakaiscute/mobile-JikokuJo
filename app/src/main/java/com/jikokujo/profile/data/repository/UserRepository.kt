package com.jikokujo.profile.data.repository

import com.jikokujo.core.data.ApiResult
import com.jikokujo.profile.data.model.User

interface UserRepository {
    var loggedInUser: ApiResult<User>?
    var userAccessToken: String?
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
    suspend fun getSignedInUser()
}