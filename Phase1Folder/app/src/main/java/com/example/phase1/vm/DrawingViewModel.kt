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
        val shape: BrushShape,
        val size: Float
    )
    var strokes by mutableStateOf(listOf<Stroke>())
        private set
    private var currentStroke: Stroke? = null
    var brushColor by mutableStateOf(Color.Blue)
        internal set
    var brushSize by mutableFloatStateOf(12f)
        internal set
    var showSettings by mutableStateOf(false)
        private set
    var brushShape = BrushShape.Square
    fun setBrushColor(color: Color) {
        brushColor = color
    }
    fun setBrushSize(size: Float) {
        brushSize = size
    }
    fun startStroke(offset: Offset) {
        currentStroke = Stroke(points = listOf(offset), color = brushColor, alpha = brushColor.alpha, shape = brushShape, size = brushSize)
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
    fun clearCanvas() {
        strokes = emptyList()
        currentStroke = null
    }
}