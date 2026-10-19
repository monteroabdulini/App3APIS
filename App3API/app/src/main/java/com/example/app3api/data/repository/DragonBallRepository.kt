package com.example.app3api.data.repository

import com.example.app3api.data.mapper.CharacterMapper
import com.example.app3api.data.remote.DragonBallApiService
import com.example.app3api.domain.CharacterModel

class DragonBallRepository(
    private val apiService: DragonBallApiService
) {
    suspend fun getCharacters(): Result<List<CharacterModel>> {
        return try {
            val response = apiService.getCharacters()
            val domainList = response.items.map { dto ->
                CharacterMapper.mapDragonBallToCharacter(dto)
            }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
