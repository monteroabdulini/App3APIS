package com.example.app3api.data.remote

import com.example.app3api.data.model.RickAndMortyResponseDto
import retrofit2.http.GET

interface RickAndMortyApiService {
    @GET("api/character")
    suspend fun getCharacters(): RickAndMortyResponseDto
}
