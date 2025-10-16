package com.example.phase1.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Stroke(
    val points: List<Offset>,
    val color: Color,
    val alpha: Float,
    val shape: BrushShape,
    val size: Float
)