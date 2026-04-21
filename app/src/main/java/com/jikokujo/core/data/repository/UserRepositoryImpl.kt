package com.jikokujo.core.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.remote.UserApi
import com.jikokujo.core.data.repository.UserRepository.Companion.toBearer
import com.jikokujo.schedule.data.model.Queryable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val dataStore: DataStore<Preferences>
): UserRepository {
    override var userAccessToken: String? = null
    companion object{
        val userAccessTokenKey: Preferences.Key<String> = stringPreferencesKey("user_access_token")
    }
    override suspend fun check(): Boolean {
        if (this.userAccessToken == null){
            getAccessToken()
        }
        if (this.userAccessToken == null){
            return false
        } else {
            val response = api.getUser(this.userAccessToken!!.toBearer())
            val isTokenValid: Boolean = response.code() / 200 < 100 && response.code() % 200 < 100

            if (isTokenValid) {
                deleteAccessToken()
            }
            return isTokenValid
        }
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

        return if (response.body()!!.errors.isEmpty()){
            ApiResult.Success(null)
        } else {
            ApiResult.Error(response.body()!!.errors.firstOrNull() ?: "Valami hiba történt.")
        }
    }
    override suspend fun login(
        email: String,
        password: String,
        remember: Boolean
    ): ApiResult<Nothing?> {
        val response = try {
            api.login(
                email = email,
                password = password,
                remember = remember
            )
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Valami hiba történt.")
        }

        if (response.body()!!.errors.isEmpty()){
            storeAccessToken(response.body()!!.data!!.userAccessToken)
            return ApiResult.Success(null)
        } else {
            return ApiResult.Error(response.body()!!.errors.firstOrNull() ?: "Valami hiba történt.")
        }
    }
    override suspend fun logout(): ApiResult<Nothing?> {
        if (!check()){
            return ApiResult.Error("Felhasználó nincs bejelentkezve.")
        }

        api.logout(this.userAccessToken!!.toBearer())
        deleteAccessToken()

        return ApiResult.Success(null)
    }
    override suspend fun getUser(): ApiResult<User> {
        if (!check()){
            return ApiResult.Error("Felhasználó nincs bejelentkezve.")
        }

        val response = try {
            api.getUser(this.userAccessToken!!.toBearer())
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Valami hiba történt.")
        }

        return if (response.body()!!.errors.isEmpty()){
            ApiResult.Success(response.body()!!.data!!.user)
        } else {
            ApiResult.Error(response.body()!!.errors.firstOrNull() ?: "Valami hiba történt.")
        }
    }
    override suspend fun getFavourites(): ApiResult<List<Favourite>> {
        if (!check()){
            return ApiResult.Error("Felhasználó nincs bejelentkezve.")
        }

        val response = try {
            api.getFavourites(this.userAccessToken!!.toBearer())
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Valami hiba történt.")
        }

        return if (response.body()!!.errors.isEmpty()){
            ApiResult.Success(response.body()!!.data!!.favourites)
        } else {
            ApiResult.Error(response.body()!!.errors.firstOrNull() ?: "Valami hiba történt.")
        }
    }

    override suspend fun toggleFavourite(routeId: String, time: Int): ApiResult<Queryable.Route?> {
        if (!check()){
            return ApiResult.Error("Felhasználó nincs bejelentkezve.")
        }

        val response = try {
            api.toggleFavourite(
                authToken = this.userAccessToken!!.toBearer(),
                routeId = routeId,
                atMins = time
            )
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Valami hiba történt.")
        }
        return if (response.body()!!.errors.isEmpty()){
            if (response.body()!!.data!!.isCreated){
                ApiResult.Success(response.body()!!.data!!.route)
            } else {
                ApiResult.Success(null)
            }
        } else {
            ApiResult.Error(response.body()!!.errors.firstOrNull() ?: "Valami hiba történt.")
        }
    }

    private suspend fun storeAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[userAccessTokenKey] = token
        }
        this.userAccessToken = token
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
        userAccessToken = null
    }
}