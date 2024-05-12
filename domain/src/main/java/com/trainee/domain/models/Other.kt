package com.trainee.domain.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Other(
    @SerializedName("official-artwork") val official_artwork: OfficialArtwork
) : Serializable
