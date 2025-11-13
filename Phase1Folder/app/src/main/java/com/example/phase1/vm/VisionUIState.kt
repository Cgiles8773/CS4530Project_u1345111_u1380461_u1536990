package com.example.phase1.vm

import com.example.phase1.data.repository.vision.VisionLabel
import com.example.phase1.data.repository.vision.VisionObject

data class VisionUiState(
    val isLoading: Boolean = false,
    val labels: List<VisionLabel> = emptyList(),
    val objects: List<VisionObject> = emptyList(),
    val errorMessage: String? = null
)
