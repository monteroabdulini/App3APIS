package com.example.app3api.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterCacheDao {

    @Query("SELECT * FROM character_cache WHERE sourceApi = :sourceApi ORDER BY addedAt ASC")
    fun observeBySource(sourceApi: String): Flow<List<CharacterCacheEntity>>

    @Query("SELECT COUNT(*) FROM character_cache WHERE sourceApi = :sourceApi")
    suspend fun countBySource(sourceApi: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterCacheEntity>)

    @Query("DELETE FROM character_cache WHERE sourceApi = :sourceApi")
    suspend fun clearBySource(sourceApi: String)
}
