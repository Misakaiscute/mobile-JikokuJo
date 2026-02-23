package com.jikokujo.core.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.remote.UserApi
import com.jikokujo.core.data.repository.UserRepository.Companion.toBearer
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

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        passwordConfirmation: String,
    ): ApiResult<Nothing?> {
        val response = try {
            api.register(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation
            )
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Sikertelen regisztráció.")
        }
        return if (response.errors.isEmpty()){
            ApiResult.Success(null)
        } else {
            ApiResult.Error(response.errors.firstOrNull() ?: "Valami hiba történt.")
        }
    }

    override suspend fun login(email: String, password: String, remember: Boolean) {
        checkAuth()
        if (this.userAccessToken != null) {
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
        checkAuth()
        this.userAccessToken?.let{
            api.logout(it.toBearer())
            deleteAccessToken()
            this.loggedInUser = null
        }
        throw IllegalAccessException("Without a logged in user logging out is impossible")
    }

    override suspend fun checkAuth() {
        if (this.userAccessToken == null){
            getAccessToken()
        }
    }

    override suspend fun getLoggedInUser() {
        checkAuth()
        this.userAccessToken?.let { token ->
            val response = try {
                api.getUser(token.toBearer())
            } catch (e: Exception) {
                this.loggedInUser = ApiResult.Error("Hiba történt.")
                Log.e("EXCEPTION", e.message.toString())
                e.printStackTrace()
                return
            }
            val isTokenInvalid: Boolean = response.code() != 200
            if (isTokenInvalid){
                deleteAccessToken()
            } else {
                this.loggedInUser = ApiResult.Success(response.body()!!.data!!.user)
            }
            return
        }
        this.loggedInUser = null
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