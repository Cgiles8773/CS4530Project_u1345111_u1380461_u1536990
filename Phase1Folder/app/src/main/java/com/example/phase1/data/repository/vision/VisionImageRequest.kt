package com.example.phase1.data.repository.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionImageRequest(
    val image: VisionImage,
    val features: List<VisionFeature>
)
