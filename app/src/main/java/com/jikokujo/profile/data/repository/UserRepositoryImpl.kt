package com.jikokujo.profile.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jikokujo.core.data.ApiResult
import com.jikokujo.profile.data.model.User
import com.jikokujo.profile.data.remote.UserApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val dataStore: DataStore<Preferences>
): UserRepository {
    override lateinit var signedInUser: ApiResult<User>
    override var userAccessToken: String? = null

    companion object{
        val userAccessTokenKey: Preferences.Key<String> = stringPreferencesKey("user_access_token")
    }

    override suspend fun register(user: User): ApiResult<Nothing> {
        TODO("Not yet implemented")
    }

    override suspend fun login(user: User): ApiResult<Nothing> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): ApiResult<Nothing> {
        TODO("Not yet implemented")
    }

    override suspend fun getSignedInUser() {
        if (userAccessToken == null){
            getAccessToken()
        }
        TODO("Not yet implemented")
    }

    private suspend fun storeAccessToken() {
        userAccessToken?.let {
            dataStore.edit { preferences ->
                preferences[userAccessTokenKey] = it
            }
        }
    }

    private suspend fun getAccessToken() {
        userAccessToken = dataStore.data.map { preferences ->
            preferences[userAccessTokenKey]
        }.first()
    }

    private suspend fun deleteAccessToken() {
        userAccessToken = null
        dataStore.edit { preferences ->
            preferences.remove(userAccessTokenKey)
        }
    }
}