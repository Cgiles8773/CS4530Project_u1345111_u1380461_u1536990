/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file contains the implementation of the ViewModel
 */
package com.example.phase1.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

// ------------------------------------------------------
// ViewModel (business + UI state)
// ------------------------------------------------------
class DrawingViewModel : ViewModel() {
    // Helper classes
    enum class BrushShape {
        Square, Circle, Triangle
    }

    data class Stroke(
        val points: List<Offset>,
        val color: Color,
        val alpha: Float,
        val shape: BrushShape
    )

    var strokes by mutableStateOf(listOf<Stroke>())
        private set
    private var currentStroke: Stroke? = null

    var brushColor by mutableStateOf(Color.Black)
        internal set
    var updateOpacity by mutableFloatStateOf(1f)
        private set

    var showSettings by mutableStateOf(false)
        private set

    // Default Brush
    var brushShape = BrushShape.Square
    fun setBrushColor(color: Color) {
        brushColor = color
    }


    fun setOpacity(value: Float) {
        updateOpacity = value
    }

    fun startStroke(offset: Offset) {
        currentStroke = Stroke(points = listOf(offset), color = brushColor, alpha = updateOpacity, shape = brushShape)
        strokes = strokes + listOf(currentStroke!!)
    }

    fun addPointToStroke(offset: Offset) {
        currentStroke = currentStroke?.copy(points = currentStroke!!.points + offset)
        strokes = strokes.dropLast(1) + listOf(currentStroke!!)
    }

    fun endStroke() {
        currentStroke = null
    }

    fun toggleSettings() {
        showSettings = !showSettings
    }
}