package com.example.phase1.data.repository.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionRequest(
    val requests: List<VisionImageRequest>
)
