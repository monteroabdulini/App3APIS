package com.example.app3api.data.mapper

import com.example.app3api.data.local.CharacterCacheEntity
import com.example.app3api.data.local.FavoriteEntity
import com.example.app3api.data.model.DemonSlayerCharacterDto
import com.example.app3api.data.model.DragonBallCharacterDto
import com.example.app3api.data.model.RickAndMortyCharacterDto
import com.example.app3api.domain.ApiSource
import com.example.app3api.domain.CharacterModel

/**
 * Mapper único para transformar los DTOs de las tres APIs
 * y las Entidades de Room a/desde el modelo de dominio unificado (CharacterModel).
 */
object CharacterMapper {

    fun mapDemonSlayerToCharacter(dto: DemonSlayerCharacterDto): CharacterModel {
        val details = buildString {
            dto.race?.let { append("Raza: $it") }
            if (dto.age != null) {
                if (isNotEmpty()) append(" | ")
                append("Edad: ${dto.age}")
            }
        }.ifEmpty { "Demon Slayer Character" }

        val desc = dto.description.takeUnless { it.isNullOrBlank() }
            ?: dto.quote?.let { "\"$it\"" }
            ?: "Sin descripción disponible."

        return CharacterModel(
            id = "ds_${dto.id ?: 0}",
            name = dto.name ?: "Personaje Desconocido",
            imageUrl = dto.img ?: "",
            gender = dto.gender ?: "Desconocido",
            statusOrRace = details,
            description = desc,
            sourceApi = ApiSource.DEMON_SLAYER
        )
    }

    fun mapRickAndMortyToCharacter(dto: RickAndMortyCharacterDto): CharacterModel {
        val statusAndSpecies = "${dto.status ?: "Estado desconocido"} • ${dto.species ?: "Especie desconocida"}"
        val desc = if (!dto.type.isNullOrBlank()) {
            "Tipo: ${dto.type}"
        } else {
            "Personaje de Rick & Morty (${dto.species ?: "Humano/Alien"})"
        }

        return CharacterModel(
            id = "rm_${dto.id ?: 0}",
            name = dto.name ?: "Personaje Desconocido",
            imageUrl = dto.image ?: "",
            gender = dto.gender ?: "Desconocido",
            statusOrRace = statusAndSpecies,
            description = desc,
            sourceApi = ApiSource.RICK_AND_MORTY
        )
    }

    fun mapDragonBallToCharacter(dto: DragonBallCharacterDto): CharacterModel {
        val raceAndAff = buildString {
            dto.race?.let { append("Raza: $it") }
            dto.affiliation?.let {
                if (isNotEmpty()) append(" • ")
                append("Afiliación: $it")
            }
        }.ifEmpty { "Personaje de Dragon Ball" }

        val desc = dto.description.takeUnless { it.isNullOrBlank() }
            ?: "Ki: ${dto.ki ?: "Desconocido"} | Máx Ki: ${dto.maxKi ?: "Desconocido"}"

        return CharacterModel(
            id = "db_${dto.id ?: 0}",
            name = dto.name ?: "Personaje Desconocido",
            imageUrl = dto.image ?: "",
            gender = dto.gender ?: "Desconocido",
            statusOrRace = raceAndAff,
            description = desc,
            sourceApi = ApiSource.DRAGON_BALL
        )
    }

    // Conversiones con Room Entities
    fun FavoriteEntity.toCharacterModel(): CharacterModel {
        val apiSourceEnum = ApiSource.entries.find { it.name == sourceApi } ?: ApiSource.DEMON_SLAYER
        return CharacterModel(
            id = id,
            name = name,
            imageUrl = imageUrl,
            gender = gender,
            statusOrRace = statusOrRace,
            description = description,
            sourceApi = apiSourceEnum
        )
    }

    fun CharacterModel.toFavoriteEntity(): FavoriteEntity {
        return FavoriteEntity(
            id = id,
            name = name,
            imageUrl = imageUrl,
            gender = gender,
            statusOrRace = statusOrRace,
            description = description,
            sourceApi = sourceApi.name
        )
    }

    fun CharacterCacheEntity.toCharacterModel(): CharacterModel {
        val apiSourceEnum = ApiSource.entries.find { it.name == sourceApi } ?: ApiSource.DEMON_SLAYER
        return CharacterModel(
            id = id,
            name = name,
            imageUrl = imageUrl,
            gender = gender,
            statusOrRace = statusOrRace,
            description = description,
            sourceApi = apiSourceEnum
        )
    }

    fun CharacterModel.toCharacterCacheEntity(): CharacterCacheEntity {
        return CharacterCacheEntity(
            id = id,
            name = name,
            imageUrl = imageUrl,
            gender = gender,
            statusOrRace = statusOrRace,
            description = description,
            sourceApi = sourceApi.name
        )
    }
}
