/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file contains the implementation of the ViewModel
 */
package com.example.phase1.vm

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.repository.ImageRepository
import com.example.phase1.model.BrushShape
import com.example.phase1.model.Stroke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val imageHandler: ImageHandler
) : ViewModel() {

    var strokes by mutableStateOf(listOf<Stroke>())
        private set
    private var currentStroke: Stroke? = null
    var background by mutableStateOf<Bitmap?>(null)
        private set
    var brushColor by mutableStateOf(Color.Blue)
        internal set
    var brushSize by mutableFloatStateOf(12f)
        internal set
    var showSettings by mutableStateOf(false)
        private set
    var showSave by mutableStateOf(false)
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

    fun toggleSave()
    {
        showSave = !showSave
    }

    fun loadDrawing(filepath: String?)
    {
        if(filepath != null)
        {
            viewModelScope.launch {
                background = imageRepository.loadBitmap(filepath)
            }
        }
    }
    fun getBitmap() : Bitmap? {
        return background
    }
    fun saveDrawing(background: Bitmap?, name: String, width: Int, height: Int) {
        viewModelScope.launch {
            val bitmap = imageHandler.saveStrokesToBitmap(background, strokes, width, height)
            imageRepository.saveImage(name, bitmap)
        }
    }
}