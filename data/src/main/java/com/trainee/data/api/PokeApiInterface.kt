package com.trainee.data.api

import com.trainee.domain.models.Pokemon
import com.trainee.domain.models.PokemonResults
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiInterface {
    @GET("pokemon/{id}")
    suspend fun getPokemon(
        @Path("id") id: Int
    ): Pokemon
    @GET("pokemon/")
    suspend fun getPokemonList(
        @Query("limit") limit: Int
    ): PokemonResults
}