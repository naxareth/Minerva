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
    val password: String
)

data class LogoutRequest(
    val token: String
)

data class LoginResponse(
    val message: String,
    val token_type: String,
    val token: String
)

data class RegisterResponse(
    val message: String,
    val token_type: String,
    val token: String
)

data class LogoutResponse(
    val message: String
)

data class ProfileResponse(
    val message: String,
    val data: User
)

data class Favorite(
    val id: Int,
    val title: String,
    val image: String,
    val user_id: Int,
    val created_at: String
)

data class FavoriteResource(
    val `anime-id`: Int,
    val title: String,
    val image: String,
    val created_at: String,
    val user_id: Int
)