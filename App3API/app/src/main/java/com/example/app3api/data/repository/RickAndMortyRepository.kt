package com.example.app3api.data.repository

import com.example.app3api.data.mapper.CharacterMapper
import com.example.app3api.data.remote.RickAndMortyApiService
import com.example.app3api.domain.CharacterModel

class RickAndMortyRepository(
    private val apiService: RickAndMortyApiService
) {
    suspend fun getCharacters(): Result<List<CharacterModel>> {
        return try {
            val response = apiService.getCharacters()
            val domainList = response.results.map { dto ->
                CharacterMapper.mapRickAndMortyToCharacter(dto)
            }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
