package com.example.phase1.data.repository.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionFeature(
    val type: String,
    val maxResults: Int = 10
)
