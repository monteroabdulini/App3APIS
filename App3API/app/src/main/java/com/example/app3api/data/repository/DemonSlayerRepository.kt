package com.example.app3api.data.repository

import com.example.app3api.data.mapper.CharacterMapper
import com.example.app3api.data.remote.DemonSlayerApiService
import com.example.app3api.domain.CharacterModel

class DemonSlayerRepository(
    private val apiService: DemonSlayerApiService
) {
    suspend fun getCharacters(): Result<List<CharacterModel>> {
        return try {
            val response = apiService.getCharacters()
            val domainList = response.content.map { dto ->
                CharacterMapper.mapDemonSlayerToCharacter(dto)
            }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
