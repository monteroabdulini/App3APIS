package com.example.app3api.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val gender: String,
    val statusOrRace: String,
    val description: String,
    val sourceApi: String,
    val addedAt: Long = System.currentTimeMillis()
)
