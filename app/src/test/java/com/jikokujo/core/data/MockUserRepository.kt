package com.jikokujo.core.data

import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.repository.UserRepository
import com.jikokujo.schedule.data.model.Queryable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MockUserRepository() : UserRepository {
    override val favourites = MutableStateFlow<List<Favourite>?>(null)
    override var userAccessToken: String? = null

    var authenticated = false
    var getUserResult: User? = null
    var getFavouritesResult: List<Favourite>? = null
    var logoutResult: ApiResult<Nothing?> = ApiResult.Success(null)
    var toggleFavouriteResult: ApiResult<Queryable.Route?> = ApiResult.Success(null)

    // Call tracking
    var logoutCalled = false
    var toggleFavouriteCalledWith: Pair<String, Int>? = null

    override suspend fun check() = authenticated
    override suspend fun getUser(): ApiResult<User> {
        return if (authenticated && getUserResult != null) {
            ApiResult.Success(getUserResult!!)
        } else {
            ApiResult.Error("Not authenticated.")
        }
    }
    override suspend fun getFavourites(): ApiResult<List<Favourite>> {
        this.favourites.update { getFavouritesResult }
        return if (authenticated && getFavouritesResult != null) {
            ApiResult.Success(getFavouritesResult!!)
        } else {
            ApiResult.Error("Not authenticated.")
        }
    }
    override suspend fun logout(): ApiResult<Nothing?> {
        logoutCalled = true
        return logoutResult
    }
    override suspend fun toggleFavourite(routeId: String, time: Int): ApiResult<Queryable.Route?> {
        toggleFavouriteCalledWith = routeId to time
        return toggleFavouriteResult
    }
    override suspend fun register(
        firstName: String, lastName: String, email: String,
        password: String, passwordConfirmation: String
    ) = ApiResult.Success(null)
    override suspend fun login(
        email: String, password: String, remember: Boolean
    ) = ApiResult.Success(null)
}