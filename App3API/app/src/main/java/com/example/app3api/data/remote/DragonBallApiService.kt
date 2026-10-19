package com.example.app3api.data.remote

import com.example.app3api.data.model.DragonBallResponseDto
import retrofit2.http.GET

interface DragonBallApiService {
    @GET("api/characters")
    suspend fun getCharacters(): DragonBallResponseDto
}
