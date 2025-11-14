package com.example.phase1.data.repository.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionObject(
    val name: String? = null,
    val score: Float? = null,
    val boundingPoly: VisionBoundingPoly? = null
)
