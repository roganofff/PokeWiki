package com.trainee.domain.usecases

import com.trainee.domain.models.Pokemon
import com.trainee.domain.repository.RepositoryInterface

class GetPokemonUseCase(private val repository: RepositoryInterface) {
    suspend fun getPokemon(id: Int): Pokemon = repository.getPokemon(id)
}