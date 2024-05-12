package com.trainee.pokewiki.di

import com.trainee.pokewiki.presentation.list.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<ListViewModel> {
        ListViewModel(
            getPokemonUseCase = get(),
            getPokemonListUseCase = get()
        )
    }
}