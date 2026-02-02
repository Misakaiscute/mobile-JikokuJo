package com.jikokujo.profile.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jikokujo.core.data.ApiResult
import com.jikokujo.profile.data.model.User
import com.jikokujo.profile.data.remote.UserApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val dataStore: DataStore<Preferences>
): UserRepository {
    override var loggedInUser: ApiResult<User>? = null
    override var userAccessToken: String? = null

    companion object{
        val userAccessTokenKey: Preferences.Key<String> = stringPreferencesKey("user_access_token")
    }

    override suspend fun register(user: User): ApiResult<Nothing?> {
        val response = try {
            api.register(user)
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Registration failed")
        }
        return if (response.errors.isEmpty()){
            ApiResult.Success(null)
        } else {
            ApiResult.Error(response.errors.firstOrNull() ?: "Something went wrong")
        }
    }

    override suspend fun login(email: String, password: String, remember: Boolean) {
        if (this.loggedInUser != null){
            return
        }
        val response = try {
            api.login(
                email = email,
                password = password,
                remember = remember
            )
        } catch (e: Exception) {
            this.loggedInUser = null
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return
        }
        this.userAccessToken = response.data!!.userAccessToken
        storeAccessToken()
    }

    override suspend fun logout() {
        this.loggedInUser = null
        this.userAccessToken = null
        deleteAccessToken()
    }

    override suspend fun getSignedInUser() {
        if (this.userAccessToken == null){
            getAccessToken()
        }
        this.userAccessToken?.let { token ->
            val response = try {
                api.getUser(token)
            } catch (e: Exception) {
                this.loggedInUser = ApiResult.Error("User not logged in yet")
                Log.e("EXCEPTION", e.message.toString())
                e.printStackTrace()
                return
            }
            this.loggedInUser = ApiResult.Success(response.data!!.user)
        }
        this.loggedInUser = ApiResult.Error("User not logged in yet")
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
        }.firstOrNull()
    }

    private suspend fun deleteAccessToken() {
        dataStore.edit { preferences ->
            preferences.remove(userAccessTokenKey)
        }
    }
}