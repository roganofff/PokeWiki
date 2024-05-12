package com.trainee.pokewiki.di

import com.trainee.domain.usecases.GetPokemonListUseCase
import com.trainee.domain.usecases.GetPokemonUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<GetPokemonUseCase> {
        GetPokemonUseCase(repository = get())
    }

    factory<GetPokemonListUseCase> {
        GetPokemonListUseCase(repository = get())
    }
}