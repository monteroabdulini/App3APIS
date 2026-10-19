package com.example.app3api.data.remote

import com.example.app3api.data.model.DemonSlayerResponseDto
import retrofit2.http.GET

interface DemonSlayerApiService {
    @GET("api/v1/characters")
    suspend fun getCharacters(): DemonSlayerResponseDto
}
