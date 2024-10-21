package com.example.minerva_10.api

import com.example.minerva_10.api.interfaces.AnimeApiService
import com.example.minerva_10.api.interfaces.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.4:8000/api/"
    private const val ANIME_API_URL = "https://animetize-api.vercel.app/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

    val animeApiService: AnimeApiService by lazy {
        retrofit.newBuilder()
            .baseUrl(ANIME_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnimeApiService::class.java)
    }
}