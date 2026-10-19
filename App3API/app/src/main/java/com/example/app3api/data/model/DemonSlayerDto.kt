package com.example.app3api.data.model

import com.google.gson.annotations.SerializedName

data class DemonSlayerResponseDto(
    @SerializedName("content")
    val content: List<DemonSlayerCharacterDto> = emptyList()
)

data class DemonSlayerCharacterDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("age")
    val age: Int? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("race")
    val race: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("img")
    val img: String? = null,
    @SerializedName("quote")
    val quote: String? = null
)
