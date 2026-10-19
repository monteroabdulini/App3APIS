package com.example.app3api.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val DEMON_SLAYER_BASE_URL = "https://www.demonslayer-api.com/"
    private const val RICK_AND_MORTY_BASE_URL = "https://rickandmortyapi.com/"
    private const val DRAGON_BALL_BASE_URL = "https://dragonball-api.com/"

    val demonSlayerApiService: DemonSlayerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DEMON_SLAYER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DemonSlayerApiService::class.java)
    }

    val rickAndMortyApiService: RickAndMortyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(RICK_AND_MORTY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyApiService::class.java)
    }

    val dragonBallApiService: DragonBallApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DRAGON_BALL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DragonBallApiService::class.java)
    }
}
