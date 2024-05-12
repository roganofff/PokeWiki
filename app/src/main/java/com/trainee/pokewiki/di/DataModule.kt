package com.trainee.pokewiki.di

import com.trainee.data.repository.Repository
import com.trainee.domain.repository.RepositoryInterface
import org.koin.dsl.module

val dataModule = module {
    single<RepositoryInterface> { Repository() }
}