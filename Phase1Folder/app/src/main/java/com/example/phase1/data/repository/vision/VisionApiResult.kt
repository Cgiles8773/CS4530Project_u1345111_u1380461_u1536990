package com.example.phase1.data.repository.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionApiResult(
    val labelAnnotations: List<VisionLabel>? = null,
    val localizedObjectAnnotations: List<VisionObject>? = null
)
