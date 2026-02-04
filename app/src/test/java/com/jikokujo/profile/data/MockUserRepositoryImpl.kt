package com.jikokujo.profile.data

import com.jikokujo.core.data.ApiResult
import com.jikokujo.profile.data.model.User
import com.jikokujo.profile.data.repository.UserRepository

class MockUserRepositoryImpl: UserRepository {
    override var loggedInUser: ApiResult<User>? = null

    override var userAccessToken: String? = null

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): ApiResult<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun login(
        email: String,
        password: String,
        remember: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun checkAuth() {
        TODO("Not yet implemented")
    }

    override suspend fun getSignedInUser() {
        TODO("Not yet implemented")
    }
}