/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file contains the implementation of the ViewModel
 */
package com.example.phase1.vm

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.repository.ImageRepository
import com.example.phase1.data.repository.VisionRepository
import com.example.phase1.data.repository.vision.VisionLabel
import com.example.phase1.data.repository.vision.VisionObject
import com.example.phase1.model.BrushShape
import com.example.phase1.model.Stroke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// -------------------------
// AI RESULT DATA CLASS
// -------------------------



@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val imageHandler: ImageHandler,
    private val visionRepository: VisionRepository       // 👈 NEW
) : ViewModel() {

    // -------------------------
    // DRAWING LOGIC (unchanged)
    // -------------------------
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

    private var orientation by mutableStateOf(Configuration.ORIENTATION_PORTRAIT)
    private var canvasSize by mutableStateOf(IntSize.Zero)

    // -------------------------
    // AI STATE
    // -------------------------
    var visionState by mutableStateOf(VisionUiState())
        private set

    // -------------------------
    // ORIENTATION LOGIC
    // -------------------------
    fun onOrientationChanged(newOrientation: Int, newCanvasSize: IntSize) {
        if (newOrientation == orientation || newCanvasSize == IntSize.Zero || canvasSize == IntSize.Zero) {
            this.orientation = newOrientation
            this.canvasSize = newCanvasSize
            return
        }

        val wasPortrait = canvasSize.width < canvasSize.height
        val isNowLandscape = newCanvasSize.width > newCanvasSize.height

        if (wasPortrait == isNowLandscape) {
            this.orientation = newOrientation
            this.canvasSize = newCanvasSize
            return
        }

        val oldCanvasSize = canvasSize

        val transformedStrokes = strokes.map { stroke ->
            val transformedPoints = stroke.points.map { point ->
                if (wasPortrait && isNowLandscape) {
                    Offset(x = point.y, y = oldCanvasSize.width - point.x)
                } else {
                    Offset(x = oldCanvasSize.height - point.y, y = point.x)
                }
            }
            stroke.copy(points = transformedPoints)
        }

        strokes = transformedStrokes
        this.orientation = newOrientation
        this.canvasSize = newCanvasSize
    }

    // -------------------------
    // DRAWING CONTROLS
    // -------------------------
    fun setBrushColor(color: Color) {
        brushColor = color
    }

    fun setBrushSize(size: Float) {
        brushSize = size
    }

    fun startStroke(offset: Offset) {
        currentStroke = Stroke(
            points = listOf(offset),
            color = brushColor,
            alpha = brushColor.alpha,
            shape = brushShape,
            size = brushSize
        )
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
        background = null
    }

    fun toggleSave() {
        showSave = !showSave
    }

    fun loadDrawing(filepath: String?) {
        if (filepath != null) {
            viewModelScope.launch {
                background = imageRepository.loadBitmap(filepath)
            }
        }
    }

    fun getBitmap(): Bitmap? = background

    fun saveDrawing(background: Bitmap?, name: String, width: Int, height: Int) {
        viewModelScope.launch {
            val bitmap = imageHandler.saveStrokesToBitmap(background, strokes, width, height)
            imageRepository.saveImage(name, bitmap)
        }
    }

    // --------------------------------------
    // AI: BITMAP → BASE64 HELPER
    // --------------------------------------
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    // --------------------------------------
    // AI: ANALYZE IMAGE FUNCTION
    // --------------------------------------

    fun loadBitmapFromPath(path: String): Bitmap? {
        return imageHandler.loadBitmapFromFile(path)
    }
    fun saveTempBitmap(bitmap: Bitmap?): String {
        if (bitmap == null) throw IllegalArgumentException("Bitmap is null")

        val filename = "analysis_temp_${System.currentTimeMillis()}.png"

        return imageHandler.saveBitmapToFile(bitmap, filename)
            ?: throw IllegalStateException("Failed to save temp bitmap")
    }

    fun analyzeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            visionState = VisionUiState(isLoading = true)

            val base64 = bitmapToBase64(bitmap)

            val result = visionRepository.analyzeImageBase64(base64)

            visionState = result.fold(
                onSuccess = { apiResult ->
                    VisionUiState(
                        isLoading = false,
                        labels = apiResult.labelAnnotations.orEmpty(),
                        objects = apiResult.localizedObjectAnnotations.orEmpty(),
                        errorMessage = null
                    )
                },
                onFailure = { e ->
                    VisionUiState(
                        isLoading = false,
                        labels = emptyList(),
                        objects = emptyList(),
                        errorMessage = e.message ?: "Unknown Vision API error"
                    )
                }
            )
        }
    }
}
