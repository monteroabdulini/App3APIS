package com.example.app3api.data.repository

import com.example.app3api.data.local.CharacterCacheDao
import com.example.app3api.data.mapper.CharacterMapper
import com.example.app3api.data.mapper.CharacterMapper.toCharacterCacheEntity
import com.example.app3api.data.mapper.CharacterMapper.toCharacterModel
import com.example.app3api.data.remote.DemonSlayerApiService
import com.example.app3api.domain.ApiSource
import com.example.app3api.domain.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DemonSlayerRepository(
    private val apiService: DemonSlayerApiService,
    private val cacheDao: CharacterCacheDao
) {
    fun observeCharacters(): Flow<List<CharacterModel>> =
        cacheDao.observeBySource(ApiSource.DEMON_SLAYER.name).map { list ->
            list.map { it.toCharacterModel() }
        }

    suspend fun hasCachedCharacters(): Boolean =
        cacheDao.countBySource(ApiSource.DEMON_SLAYER.name) > 0

    suspend fun fetchAndCacheCharacters(): Result<Unit> {
        return try {
            val response = apiService.getCharacters()
            val domainList = response.content.map { dto ->
                CharacterMapper.mapDemonSlayerToCharacter(dto)
            }
            cacheDao.insertAll(domainList.map { it.toCharacterCacheEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
