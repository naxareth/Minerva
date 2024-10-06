package com.example.minerva_10.api

import com.example.minerva_10.api.responses.Favorite
import com.example.minerva_10.api.responses.FavoriteResource
import com.example.minerva_10.api.responses.FavoriteResponse
import com.example.minerva_10.api.responses.LoginRequest
import com.example.minerva_10.api.responses.LoginResponse
import com.example.minerva_10.api.responses.LogoutResponse
import com.example.minerva_10.api.responses.ProfileResponse
import com.example.minerva_10.api.responses.RegisterRequest
import com.example.minerva_10.api.responses.RegisterResponse
import com.example.minerva_10.api.responses.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    // Authentication Endpoints
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("logout")
    @Headers("Authorization: Bearer {token}")
    fun logout(@Path("token") token: String): Call<LogoutResponse>

    @GET("profile")
    @Headers("Authorization: Bearer {token}")
    fun profile(@Path("token") token: String): Call<ProfileResponse>

    // Favorites Endpoints
    @GET("favorites")
    fun getFavorites(@Header("Authorization") token: String): Call<FavoriteResponse>

    @POST("favorites")
    @Headers("Authorization: Bearer {token}")
    fun createFavorite(@Path("token") token: String, @Body favorite:  Favorite): Call<FavoriteResource>

    @GET("favorites/{id}")
    @Headers("Authorization: Bearer {token}")
    fun getFavorite(@Path("token") token: String, @Path("id") id: Int): Call<FavoriteResource>

    @PUT("favorites/{id}")
    @Headers("Authorization: Bearer {token}")
    fun updateFavorite(@Path("token") token: String, @Path("id") id: Int, @Body favorite: Favorite): Call<FavoriteResource>

    @DELETE("favorites/{id}")
    @Headers("Authorization: Bearer {token}")
    fun deleteFavorite(@Path("token") token: String, @Path("id") id: Int): Call<Void>
}