package com.example.app3api.domain

data class CharacterModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val gender: String,
    val statusOrRace: String,
    val description: String,
    val sourceApi: ApiSource
)

enum class ApiSource(val title: String) {
    DEMON_SLAYER("Demon Slayer"),
    RICK_AND_MORTY("Rick & Morty"),
    DRAGON_BALL("Dragon Ball")
}
