package com.jikokujo.core.data.remote

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

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
    ): Response<ResponseRoot<EmptyPayload>>

    @FormUrlEncoded
    @Headers("accept: application/json")
    @POST("user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("remember") remember: Boolean = false
    ): Response<ResponseRoot<UserLoginObj>>

    @POST("user/logout")
    suspend fun logout(
        @Header("Authorization") authToken: String
    ): Response<ResponseRoot<EmptyPayload>>

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
        @Field("time") atMins: Int
    ): Response<ResponseRoot<ToggleFavouriteObj>>

    @FormUrlEncoded
    @Headers("accept: application/json")
    @GET("user/favourites")
    suspend fun getFavourites(
        @Header("Authorization") authToken: String,
    ): Response<ResponseRoot<GetFavouritesObj>>
}