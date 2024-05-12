package com.trainee.domain.models

import java.io.Serializable

data class Pokemon (
    var id: Int,
    var name: String,
    val base_experience: Int,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val stats: List<Stat>,
    var dominant_color: Int?,
    var capture_rate: Int,
    var url: String? = ""
) : Serializable
