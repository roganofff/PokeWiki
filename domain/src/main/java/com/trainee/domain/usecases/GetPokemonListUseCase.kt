package com.trainee.domain.usecases

import com.trainee.domain.models.PokemonResults
import com.trainee.domain.repository.RepositoryInterface

class GetPokemonListUseCase(private val repository: RepositoryInterface) {
    suspend fun getPokemonList(limit: Int): PokemonResults = repository.getPokemonList(limit)
}