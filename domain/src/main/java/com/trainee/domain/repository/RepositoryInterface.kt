package com.trainee.domain.repository

import com.trainee.domain.models.Pokemon
import com.trainee.domain.models.PokemonResults

interface RepositoryInterface {
    suspend fun getPokemon(id: Int): Pokemon
    suspend fun getPokemonList(limit: Int): PokemonResults
}