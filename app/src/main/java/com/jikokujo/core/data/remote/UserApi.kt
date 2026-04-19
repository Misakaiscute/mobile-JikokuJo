package com.jikokujo.core.data.remote

import com.jikokujo.core.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @FormUrlEncoded
    @Headers("accept: application/json")
    @POST("user/register")
    suspend fun register(
        @Field("first_name") firstName: String,
        @Field("second_name") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
    ): ResponseRoot<EmptyPayload>

    @FormUrlEncoded
    @Headers("accept: application/json")
    @POST("user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("remember") remember: Boolean = false
    ): ResponseRoot<UserLoginObj>

    @GET("user/logout")
    suspend fun logout(
        @Header("Authorization") authToken: String
    ): Response<ResponseRoot<EmptyPayload>> //TODO: Not yet implemented on the backend

    @Headers("accept: application/json")
    @GET("user")
    suspend fun getUser(
        @Header("Authorization") authToken: String
    ): Response<ResponseRoot<GetUserObj>>

    @FormUrlEncoded
    @Headers("accept: application/json")
    @POST("routes/favourite/toggle")
    suspend fun toggleFavourite(
        @Header("Authorization") authToken: String,
        @Field("route_id") routeId: String,
        @Field("minutes") minutes: Int
    ): Response<ResponseRoot<EmptyPayload>>

    @Headers("accept: application/json")
    @FormUrlEncoded
    @GET("favourites")
    suspend fun getFavourites(
        @Header("Authorization") authToken: String,
    ): Response<ResponseRoot<EmptyPayload>> //TODO: Change to correct return type once available
}