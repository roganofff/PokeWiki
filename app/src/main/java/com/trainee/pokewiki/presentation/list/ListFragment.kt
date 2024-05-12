package com.trainee.pokewiki.presentation.list

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trainee.data.utils.Constants
import com.trainee.domain.models.Pokemon
import com.trainee.pokewiki.R
import com.trainee.pokewiki.databinding.FragmentListBinding
import com.trainee.pokewiki.presentation.common.PokeRecyclerViewAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

private var binding: FragmentListBinding? = null
private lateinit var viewBinding: FragmentListBinding
var pokemonsDisplayed: Int = 0
var loading: Boolean = false
var failureFlag = false
var allTypesList: MutableList<Pokemon> = mutableListOf()
var toLoadList: MutableList<Pokemon> = mutableListOf()
var pagingFlag: Boolean = true

class ListFragment : Fragment() {
    private val viewModel by viewModel<ListViewModel>()
    private lateinit var adapter: PokeRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokemonsDisplayed = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = FragmentListBinding.inflate(inflater, container, false)
        }
        viewBinding = binding!!
        setupViews()
        setupObservers()
        return binding?.root
    }

    private fun onLoadingSuccess(pokemonList: MutableList<Pokemon>, adapter: PokeRecyclerViewAdapter) {
        with(viewBinding) {
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            noInternetLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        adapter.submitList(pokemonList.toMutableList())
        pokemonsDisplayed = pokemonList.size
        loading = false
    }

    private fun onLoadingFailure() {
        with(viewBinding) {
            if (pokemonsDisplayed == 0) noInternetLayout.visibility = View.VISIBLE
            else Toast.makeText(context, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT)
                .show()
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
        }
        failureFlag = true
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let {
            it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    onConnectionRestored()
                }
            })
        }
    }

    private fun setupObservers() {
        viewModel.pokemon.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ListViewModel.Result.Failure -> onLoadingFailure()
                ListViewModel.Result.Loading -> loading = true
                is ListViewModel.Result.Success -> onLoadingSuccess(result.value, adapter)
            }
        }
        viewModel.pokemonList.observe(viewLifecycleOwner) { result ->
            if (result is ListViewModel.Result.Success && allTypesList.isEmpty()) {
                toLoadList.clear()
                result.value.results.forEach {
                    val trimmedUrl = it.url?.dropLast(1)
                    it.id = trimmedUrl!!.substring(trimmedUrl.lastIndexOf("/") + 1).toInt()
                    if (it.id <= Constants.TOTAL_POKEMONS) allTypesList.add(it)
                }
                setupAdapter(allTypesList.size)
                for (i in 0 until Constants.LOAD_LIMIT) {
                    toLoadList.add(result.value.results[i])
                }
                viewModel.getPokemon(toLoadList)
            }
        }
    }

    private fun setupViews() {
        with(viewBinding) {
            setupAdapter(allTypesList.size)
            showLoadingAnimation()
            customToolbarTextview.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.title_color
                )
            )

        }
    }

    private fun setupAdapter(lastPosition: Int) {
        adapter = PokeRecyclerViewAdapter(clickListener = {
            val action = ListFragmentDirections.actionListFragmentToDetailFragment(it)
            findNavController(requireView()).navigate(action)
        }, true, lastPosition)
        setupRecycler(adapter)
    }

    private fun setupRecycler(adapter: PokeRecyclerViewAdapter) {
        with(viewBinding) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager =
                GridLayoutManager(context, resources.getInteger(R.integer.grid_column_count))
            recyclerView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.bg_color
                )
            )
            recyclerView.hasFixedSize()
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (pagingFlag) callGetPokemon()
                }
            })
        }
    }

    private fun showLoadingAnimation() {
        if (pokemonsDisplayed == 0) {
            viewBinding.shimmerLayout.startShimmer()
            viewBinding.shimmerLayout.visibility = View.VISIBLE
        }
    }

    private fun callGetPokemon() {
        if (!viewBinding.recyclerView.canScrollVertically(1) && !loading && pokemonsDisplayed >= Constants.LOAD_LIMIT) {
            toLoadList.clear()
            if (allTypesList.size - pokemonsDisplayed >= 20) {
                for (i in pokemonsDisplayed until pokemonsDisplayed + Constants.LOAD_LIMIT) {
                    toLoadList.add(allTypesList[i])
                }
                pokemonsDisplayed += Constants.LOAD_LIMIT
            } else if (pokemonsDisplayed < allTypesList.size) {
                for (i in pokemonsDisplayed until allTypesList.size) {
                    toLoadList.add(allTypesList[i])
                }
                pokemonsDisplayed = allTypesList.size
            }
            viewModel.getPokemon(toLoadList)
        }
    }

    private fun onConnectionRestored() {
        if (failureFlag) {
            activity?.runOnUiThread { viewBinding.noInternetLayout.visibility = View.GONE }
            if (toLoadList.isNotEmpty()) {
                viewModel.getPokemon(toLoadList)
                activity?.runOnUiThread { showLoadingAnimation() }
                failureFlag = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        allTypesList.clear()
    }
}

