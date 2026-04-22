package com.jikokujo.core.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.remote.EmptyPayload
import com.jikokujo.core.data.remote.UserApi
import com.jikokujo.core.data.repository.UserRepository.Companion.toBearer
import com.jikokujo.core.utils.errorAs
import com.jikokujo.schedule.data.model.Queryable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import okhttp3.internal.toImmutableList
import retrofit2.HttpException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val dataStore: DataStore<Preferences>
): UserRepository {
    private val favourites: MutableList<Favourite>? = null
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
            try {
                api.getUser(this.userAccessToken!!.toBearer())
            } catch (_: Exception) {
                deleteAccessToken()
                return false
            }

            return true
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        passwordConfirmation: String,
    ): ApiResult<Nothing?> {
        try {
            api.register(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation
            )
        } catch (e: HttpException){
            return when (e.code()){
                422 -> ApiResult.Error("Ez az email cím már használatban van.")
                else -> ApiResult.Error("Valami hiba történt.")
            }
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Sikertelen regisztráció.")
        }
        return ApiResult.Success(null)
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
        } catch (_: HttpException){
            return ApiResult.Error("Hibás email cím vagy jelszó.")
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Szerver nem elérhető.")
        }

        storeAccessToken(response.data!!.userAccessToken)
        return ApiResult.Success(null)
    }
    override suspend fun logout(): ApiResult<Nothing?> {
        if (!check()){
            return ApiResult.Error("Felhasználó nincs bejelentkezve.")
        }
        try {
            api.logout(this.userAccessToken!!.toBearer())
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Valami hiba történt.")
        }

        deleteAccessToken()
        return ApiResult.Success(null)
    }
    override suspend fun getUser(): ApiResult<User> {
        if (!check()){
            return ApiResult.Error("Felhasználó nincs bejelentkezve.")
        }

        val response = try {
            api.getUser(this.userAccessToken!!.toBearer())
        } catch (e: HttpException){
            return try {
                ApiResult.Error(e.errorAs<EmptyPayload>().errors.firstOrNull() ?: "Valami hiba történt.")
            } catch (_: Exception) {
                ApiResult.Error("Valami hiba történt.")
            }
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Szerver nem elérhető.")
        }

        return ApiResult.Success(response.data!!.user)
    }
    override suspend fun getFavourites(): ApiResult<List<Favourite>> {
        if (!check()){
            return ApiResult.Error("Felhasználó nincs bejelentkezve.")
        }
        if (this.favourites != null && this.favourites.count() > 0) {
            return ApiResult.Success(this.favourites.toImmutableList())
        }

        val response = try {
            api.getFavourites(this.userAccessToken!!.toBearer())
        } catch (e: HttpException){
            return try {
                ApiResult.Error(e.errorAs<EmptyPayload>().errors.firstOrNull() ?: "Valami hiba történt.")
            } catch (_: Exception) {
                ApiResult.Error("Valami hiba történt.")
            }
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Szerver nem elérhető.")
        }

        this.favourites!!.clear()
        this.favourites.addAll(response.data!!.favourites)
        return ApiResult.Success(response.data.favourites)
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
        } catch (e: HttpException) {
            return try {
                ApiResult.Error(e.errorAs<EmptyPayload>().errors.firstOrNull() ?: "Valami hiba történt.")
            } catch (_: Exception) {
                ApiResult.Error("Valami hiba történt.")
            }
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return ApiResult.Error("Szerver nem elérhető.")
        }

        if (response.data!!.isCreated){
            this.favourites!!.add(Favourite(
                route = response.data.route,
                atMins = time
            ))
            return ApiResult.Success(response.data.route)
        } else {
            val filtered: List<Favourite> = this.favourites!!.filterNot {
                return@filterNot it.route.id == response.data.route.id && it.atMins == time
            }
            this.favourites.clear()
            this.favourites.addAll(filtered)
            return ApiResult.Success(null)
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