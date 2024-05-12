package com.trainee.data.repository

import com.trainee.data.api.RetrofitInstance
import com.trainee.domain.models.Pokemon
import com.trainee.domain.models.PokemonResults
import com.trainee.domain.repository.RepositoryInterface

class Repository : RepositoryInterface {

    override suspend fun getPokemon(id: Int): Pokemon {
        return RetrofitInstance.pokeApi.getPokemon(id)
    }

    override suspend fun getPokemonList(limit: Int): PokemonResults {
        return RetrofitInstance.pokeApi.getPokemonList(limit)
    }
}