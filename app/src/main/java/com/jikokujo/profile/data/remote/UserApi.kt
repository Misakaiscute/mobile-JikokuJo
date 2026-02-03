package com.jikokujo.profile.data.remote

import com.jikokujo.core.data.EmptyPayload
import com.jikokujo.core.data.ResponseRoot
import com.jikokujo.profile.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @Headers("accept: application/json")
    @POST("user/register")
    suspend fun register(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
    ): ResponseRoot<EmptyPayload>

    @Headers("accept: application/json")
    @POST("user/login/{remember}")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Path("remember") remember: Boolean = false
    ): ResponseRoot<UserLoginObj>

    @Headers("accept: application/json")
    @POST("user")
    suspend fun getUser(
        @Header("Authorization") authToken: String
    ): ResponseRoot<GetUserObj>

    @Headers("accept: application/json")
    @PUT("user/update")
    suspend fun updateUser(
        @Header("Authorization") authToken: String,
        @Body user: User
    ): ResponseRoot<EmptyPayload>

    @Headers("accept: application/json")
    @DELETE("user/delete")
    suspend fun deleteUser(
        @Header("Authorization") authToken: String
    ): ResponseRoot<EmptyPayload>

    @Headers("accept: application/json")
    @DELETE("user/delete")
    suspend fun toggleFavourites(
        @Header("Authorization") authToken: String,
        @Field("route_id") routeId: String,
        @Field("minutes") minutes: Int
    ): ResponseRoot<EmptyPayload>
}