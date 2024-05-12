package com.trainee.pokewiki.presentation.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainee.data.utils.Constants
import com.trainee.domain.models.Pokemon
import com.trainee.domain.models.PokemonResults
import com.trainee.domain.usecases.GetPokemonListUseCase
import com.trainee.domain.usecases.GetPokemonUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ListViewModel(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val getPokemonListUseCase: GetPokemonListUseCase,
): ViewModel() {

    private var coroutineExceptionHandler: CoroutineExceptionHandler
    private var job: Job = Job()
    private val list: MutableList<Pokemon> = mutableListOf()

    private val _pokemon: MutableLiveData<Result<MutableList<Pokemon>>> = MutableLiveData()
    val pokemon: LiveData<Result<MutableList<Pokemon>>> = _pokemon

    private val _pokemonList: MutableLiveData<Result<PokemonResults>> = MutableLiveData()
    val pokemonList: LiveData<Result<PokemonResults>>
        get() = _pokemonList

    init {
        coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            _pokemon.value = Result.Failure(exception)
        }
        pokemonsDisplayed = 0
        loadPokemonList(Constants.TOTAL_POKEMONS)
    }

    fun getPokemon(pokemonList: MutableList<Pokemon>) {
        cancelJobIfRunning()
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _pokemon.value = Result.Loading
            coroutineScope {
                pokemonList.forEach {
                    launch(coroutineExceptionHandler) {
                        if (!containsPokemon(list, it)) {
                            list.add(getPokemonUseCase.getPokemon(it.id))
                            Log.d("AAA", list.get(0).toString())
                        }
                    }
                }
            }
            list.sortBy { it.id }
            _pokemon.value = Result.Success(list)
        }
    }

    private fun loadPokemonList(limit: Int) {
        cancelJobIfRunning()
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _pokemonList.value = Result.Loading
            _pokemonList.value = Result.Success(getPokemonListUseCase.getPokemonList(limit))
        }
    }

    private fun containsPokemon(pokemonList: MutableList<Pokemon>, pokemon: Pokemon): Boolean {
        pokemonList.forEach {
            if (it.id == pokemon.id) return true
        }
        return false
    }

    private fun cancelJobIfRunning() {
        if (job.isActive) {
            job.cancel()
        }
    }

    sealed class Result<out T : Any> {
        data class Success<out T : Any>(val value: T) : Result<T>()
        data object Loading : Result<Nothing>()
        data class Failure(val throwable: Throwable) : Result<Nothing>()
    }
}