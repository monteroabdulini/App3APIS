package com.example.app3api.data.model

import com.google.gson.annotations.SerializedName

data class DragonBallResponseDto(
    @SerializedName("items")
    val items: List<DragonBallCharacterDto> = emptyList()
)

data class DragonBallCharacterDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("ki")
    val ki: String? = null,
    @SerializedName("maxKi")
    val maxKi: String? = null,
    @SerializedName("race")
    val race: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("affiliation")
    val affiliation: String? = null
)
