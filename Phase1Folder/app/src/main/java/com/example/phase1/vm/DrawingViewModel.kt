/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * ViewModel for drawing, saving, loading, and AI analysis.
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
import com.example.phase1.model.BrushShape
import com.example.phase1.model.Stroke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val imageHandler: ImageHandler,
    private val visionRepository: VisionRepository
) : ViewModel() {

    // Drawing state
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

    // Orientation state
    private var orientation by mutableStateOf(Configuration.ORIENTATION_PORTRAIT)
    private var canvasSize by mutableStateOf(IntSize.Zero)

    // AI state
    var visionState by mutableStateOf(VisionUiState())
        private set

    // Handle canvas transformation on orientation change
    fun onOrientationChanged(newOrientation: Int, newCanvasSize: IntSize) {
        if (newOrientation == orientation ||
            newCanvasSize == IntSize.Zero ||
            canvasSize == IntSize.Zero
        ) {
            orientation = newOrientation
            canvasSize = newCanvasSize
            return
        }

        val wasPortrait = canvasSize.width < canvasSize.height
        val nowLandscape = newCanvasSize.width > newCanvasSize.height

        if (wasPortrait == nowLandscape) {
            orientation = newOrientation
            canvasSize = newCanvasSize
            return
        }

        val oldSize = canvasSize
        strokes = strokes.map { stroke ->
            val newPoints = stroke.points.map { p ->
                if (wasPortrait && nowLandscape) {
                    Offset(p.y, oldSize.width - p.x)
                } else {
                    Offset(oldSize.height - p.y, p.x)
                }
            }
            stroke.copy(points = newPoints)
        }

        orientation = newOrientation
        canvasSize = newCanvasSize
    }

    // Drawing controls
    fun setBrushColor(color: Color) { brushColor = color }
    fun setBrushSize(size: Float) { brushSize = size }

    fun startStroke(offset: Offset) {
        currentStroke = Stroke(
            points = listOf(offset),
            color = brushColor,
            alpha = brushColor.alpha,
            shape = brushShape,
            size = brushSize
        )
        strokes = strokes + currentStroke!!
    }

    fun addPointToStroke(offset: Offset) {
        currentStroke = currentStroke?.copy(
            points = currentStroke!!.points + offset
        )
        strokes = strokes.dropLast(1) + currentStroke!!
    }

    fun endStroke() {
        currentStroke = null
    }

    fun toggleSettings() { showSettings = !showSettings }
    fun toggleSave() { showSave = !showSave }

    fun clearCanvas() {
        strokes = emptyList()
        currentStroke = null
        background = null
    }

    // Load drawing bitmap
    fun loadDrawing(filepath: String?) {
        if (filepath != null) {
            viewModelScope.launch {
                background = imageRepository.loadBitmap(filepath)
            }
        }
    }

    fun getBitmap(): Bitmap? = background

    // Save drawing to storage
    fun saveDrawing(background: Bitmap?, name: String, width: Int, height: Int) {
        viewModelScope.launch {
            val bmp = imageHandler.saveStrokesToBitmap(background, strokes, width, height)
            imageRepository.saveImage(name, bmp)
        }
    }

    // Load bitmap from file (used by AnalysisScreen)
    fun loadBitmapFromPath(path: String): Bitmap? {
        return imageHandler.loadBitmapFromFile(path)
    }

    // Save temp image for AI analysis
    fun saveTempBitmap(bitmap: Bitmap?): String {
        require(bitmap != null) { "Bitmap is null" }

        val filename = "analysis_temp_${System.currentTimeMillis()}.png"

        return imageHandler.saveBitmapToFile(bitmap, filename)
            ?: error("Failed to save temp bitmap")
    }

    // Convert bitmap to Base64
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    // Run Google Vision analysis
    fun analyzeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            visionState = VisionUiState(isLoading = true)

            val base64 = bitmapToBase64(bitmap)
            val result = visionRepository.analyzeImageBase64(base64)

            visionState = result.fold(
                onSuccess = { api ->
                    VisionUiState(
                        isLoading = false,
                        labels = api.labelAnnotations.orEmpty(),
                        objects = api.localizedObjectAnnotations.orEmpty(),
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
