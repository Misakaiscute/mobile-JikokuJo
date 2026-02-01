package com.jikokujo.profile.data.repository

import com.jikokujo.core.data.ApiResult
import com.jikokujo.profile.data.model.User

interface UserRepository {
    var signedInUser: ApiResult<User>
    var userAccessToken: String?
    suspend fun register(user: User): ApiResult<Nothing>
    suspend fun login(user: User): ApiResult<Nothing>
    suspend fun logout(): ApiResult<Nothing>
    suspend fun getSignedInUser()
}