package com.example.minerva_10.api.responses

data class User(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class LogoutRequest(
    val token: String
)

data class LoginResponse(
    val message: String,
    val token_type: String,
    val token: String,
    val userId: Int
)

data class RegisterResponse(
    val message: String,
    val token_type: String,
    val token: String
)

data class LogoutResponse(
    val message: String
)

data class OtpRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class ChangePasswordRequest(
    val email: String,
    val otp: String,
    val password: String,
    val password_confirmation: String
)

data class ChangePasswordResponse(
    val message: String
)

data class ProfileResponse(
    val message: String,
    val data: User
)

data class Favorite(
    val anime_id: String = "",
    val title: String,
    val image: String,
    val user_id: Int,
)

data class FavoriteResource(
    val `anime_id`: String,
    val title: String,
    val image: String,
    val user_id: Int
)

data class FavoriteResponse(
    val data: List<FavoriteResource>
)