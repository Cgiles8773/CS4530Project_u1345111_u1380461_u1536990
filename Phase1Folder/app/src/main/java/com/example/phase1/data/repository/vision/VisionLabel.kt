package com.example.phase1.data.repository.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionLabel(
    val description: String? = null,
    val score: Float? = null
)
