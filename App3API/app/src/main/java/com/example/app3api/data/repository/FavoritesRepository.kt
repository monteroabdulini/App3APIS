package com.example.app3api.data.repository

import com.example.app3api.data.local.FavoriteDao
import com.example.app3api.data.mapper.CharacterMapper.toCharacterModel
import com.example.app3api.data.mapper.CharacterMapper.toFavoriteEntity
import com.example.app3api.domain.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(private val dao: FavoriteDao) {

    fun observeFavorites(): Flow<List<CharacterModel>> =
        dao.observeFavorites().map { entities ->
            entities.map { it.toCharacterModel() }
        }

    fun observeFavoriteIds(): Flow<Set<String>> =
        dao.observeFavoriteIds().map { ids -> ids.toSet() }

    suspend fun addFavorite(character: CharacterModel) =
        dao.insert(character.toFavoriteEntity())

    suspend fun removeFavorite(id: String) =
        dao.deleteById(id)
}
