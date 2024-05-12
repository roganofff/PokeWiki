package com.trainee.pokewiki.presentation.detail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.trainee.pokewiki.databinding.FragmentDetailBinding
import com.trainee.domain.models.Pokemon
import com.trainee.pokewiki.R
import org.koin.androidx.viewmodel.ext.android.viewModel

private lateinit var viewBinding: FragmentDetailBinding

class DetailFragment : Fragment() {
    private val viewModel by viewModel<DetailViewModel>()
    private var dominantColor = Color.GRAY

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.setShowHideAnimationEnabled(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentDetailBinding.inflate(inflater, container, false)
        arguments?.let { bundle ->
            setData(DetailFragmentArgs.fromBundle(bundle).pokemon)
        }
        return viewBinding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        activity?.window?.statusBarColor = dominantColor
        activity?.window?.navigationBarColor = dominantColor
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.bg_color)
        activity?.window?.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.bg_color)
    }

    private fun setData(pokemon: Pokemon) {
        with(viewBinding) {
            pokeId.text = requireContext().getString(R.string.pokemon_number_format, pokemon.id)
            pokeName.text = pokemon.name
            pokeCaptureRate.text = pokemon.capture_rate.toString()
            pokeXp.text = pokemon.base_experience.toString()
            pokeHeight.text = getString(R.string.height_format, (pokemon.height.times(10)))
            pokeWeight.text = getString(R.string.weight_format, (pokemon.weight.div(10.0)))
            pokeHp.text = pokemon.stats[0].base_stat.toString()
            pokeAttack.text = pokemon.stats[1].base_stat.toString()
            pokeDefense.text = pokemon.stats[2].base_stat.toString()
            pokeSpecialAttack.text = pokemon.stats[3].base_stat.toString()
            pokeSpecialDefense.text = pokemon.stats[4].base_stat.toString()
            pokeSpeed.text = pokemon.stats[5].base_stat.toString()
            pokeBack.setOnClickListener { activity?.onBackPressed() }
            loadImage(pokeInfoImage, pokemon.sprites.other.official_artwork.front_default)
            dominantColor = pokemon.dominant_color!!
            pokeScrollView.setBackgroundColor(dominantColor)
            activity?.window?.statusBarColor = dominantColor
        }
    }

    private fun loadImage(pokeInfoImage: ImageView, imageUrl: String) {
        Glide.with(requireContext().applicationContext)
            .asBitmap()
            .load(imageUrl)
            .centerCrop()
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(pokeInfoImage)
    }
}