package com.example.minerva_10.api.interfaces

import com.example.minerva_10.api.responses.ChangePasswordRequest
import com.example.minerva_10.api.responses.ChangePasswordResponse
import com.example.minerva_10.api.responses.Favorite
import com.example.minerva_10.api.responses.FavoriteResource
import com.example.minerva_10.api.responses.FavoriteResponse
import com.example.minerva_10.api.responses.LoginRequest
import com.example.minerva_10.api.responses.LoginResponse
import com.example.minerva_10.api.responses.LogoutResponse
import com.example.minerva_10.api.responses.OtpRequest
import com.example.minerva_10.api.responses.ProfileResponse
import com.example.minerva_10.api.responses.RegisterRequest
import com.example.minerva_10.api.responses.RegisterResponse
import com.example.minerva_10.api.responses.VerifyOtpRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiInterface {
    // Authentication Endpoints
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<LogoutResponse>

    @POST("password-recovery/send-otp")
    fun sendOtp(@Body otpRequest: OtpRequest): Call<Void>

    @POST("password-recovery/verify-otp")
    fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): Call<Void>

    @POST("password-recovery/change-password")
    fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Call<ChangePasswordResponse>

    @GET("profile")
    fun profile(@Header("Authorization") token: String): Call<ProfileResponse>

    // Favorites Endpoints
    @GET("favorites")
    fun getFavorites(@Header("Authorization") token: String): Call<FavoriteResponse>

    @POST("favorites")
    fun createFavorite(
        @Header("Authorization") token: String,
        @Body favorite: Favorite
    ): Call<FavoriteResource>

    @GET("favorites/{id}")
    @Headers("Authorization: Bearer {token}")
    fun getFavorite(@Path("token") token: String, @Path("id") id: Int): Call<FavoriteResource>

    @PUT("favorites/{id}")
    @Headers("Authorization: Bearer {token}")
    fun updateFavorite(
        @Path("token") token: String,
        @Path("id") id: Int,
        @Body favorite: Favorite
    ): Call<FavoriteResource>

    @DELETE("favorites/{id}")
    fun deleteFavorite(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>
}
