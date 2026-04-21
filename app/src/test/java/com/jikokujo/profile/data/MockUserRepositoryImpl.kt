package com.jikokujo.profile.data

import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.repository.UserRepository
import com.jikokujo.schedule.data.model.Queryable

class MockUserRepositoryImpl: UserRepository {
    override var userAccessToken: String? = null
    override suspend fun check(): Boolean {
        TODO("Not yet implemented")
    }

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
    ): ApiResult<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): ApiResult<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(): ApiResult<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getFavourites(): ApiResult<List<Favourite>> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleFavourite(
        routeId: String,
        time: Int
    ): ApiResult<Queryable.Route?> {
        TODO("Not yet implemented")
    }
}