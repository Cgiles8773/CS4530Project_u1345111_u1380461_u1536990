package com.example.phase1.data.repository.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionBoundingPoly(
    val normalizedVertices: List<VisionVertex>? = null
)
