
package com.example.phase1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phase1.ui.theme.Phase1Theme
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlin.math.sqrt

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



// ------------------------------------------------------
// ViewModel (business + UI state)
// ------------------------------------------------------
class DrawingViewModel : ViewModel() {


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


// ------------------------------------------------------
// Activity (entry point, wires ViewModel + View)
// ------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Phase1Theme {
                val viewModel: DrawingViewModel = viewModel()
                MainScreen(viewModel)
            }
        }
    }
}

// ------------------------------------------------------
// Main Screen Composable (View layer)
// ------------------------------------------------------
@Composable
fun MainScreen(viewModel: DrawingViewModel) {
    Scaffold(
        floatingActionButton = {
            if (!viewModel.showSettings) {
                FloatingActionButton(onClick = { viewModel.toggleSettings() }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Brush Settings")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize(0.95f)
                    .border(5.dp, Color.Black)
            ) {
                DrawingCanvas(viewModel)
            }

            if (viewModel.showSettings) {
                SettingsWindow(
                    viewModel = viewModel,
                    onDismiss = { viewModel.toggleSettings() }
                )
            }
        }
    }
}


// ------------------------------------------------------
// Settings Window Composable
// ------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsWindow(viewModel: DrawingViewModel, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Brush Settings", style = MaterialTheme.typography.titleMedium)

                ColorPicker(onColorSelected = { viewModel.setBrushColor(it) })

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //Square
                    Button(onClick = {viewModel.brushShape = (BrushShape.Square)}, modifier = Modifier.size(64.dp)) {
                        Box(
                            modifier = Modifier
                                .drawBehind {
                                    val side = size.height * .65f
                                    val left = (size.width - side) / 2
                                    val top = (size.height - side) / 2
                                    drawRect(
                                        color = Color.White,
                                        topLeft = Offset(left, top),
                                        size = Size(side, side)
                                    )
                                }
                                .fillMaxSize()
                        )
                    }

                    //Circle
                    Button(onClick = {viewModel.brushShape = (BrushShape.Circle)}, modifier = Modifier.size(64.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(color = Color.White, radius = size.minDimension)
                        }
                    }
                    //Triangle
                    Button(onClick = {viewModel.brushShape = (BrushShape.Triangle)}, modifier = Modifier.size(64.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    // Side length (use minDimension so triangle fits); shrink a bit for padding
                                    val s = size.height * 0.65f

                                    // Height of an equilateral triangle: h = s * sqrt(3) / 2
                                    val h = (s * sqrt(3.0) / 2.0).toFloat()

                                    // Center of the box
                                    val cx = size.width / 2f
                                    val cy = size.height / 2f + 10f

                                    // If centroid is at (cx, cy):
                                    // apex Y = cy - 2h/3, base Y = cy + h/3
                                    val apexX = cx
                                    val apexY = cy - (2f * h / 3f)
                                    val leftX = cx - s / 2f
                                    val leftY = cy + h / 3f
                                    val rightX = cx + s / 2f
                                    val rightY = leftY

                                    val path = Path().apply {
                                        moveTo(apexX, apexY)
                                        lineTo(leftX, leftY)
                                        lineTo(rightX, rightY)
                                        close()
                                    }

                                    drawPath(path, color = Color.White)
                                }
                        )
                    }
                }

                // Close button
                Button(onClick = { onDismiss() }, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
            }
        }
    }
}

// ------------------------------------------------------
// Drawing Canvas Composable
// ------------------------------------------------------
@Composable
fun DrawingCanvas(viewModel: DrawingViewModel) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clipToBounds()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> viewModel.startStroke(offset) },
                    onDrag = { change, _ ->
                        change.consume()
                        viewModel.addPointToStroke(change.position)
                    },
                    onDragEnd = { viewModel.endStroke() }
                )
            }
    ) {
        viewModel.strokes.forEach { stroke ->
            val points = stroke.points
            for (i in 0 until points.size - 1) {

                // Draw based on the shape selected
                when (stroke.shape) {

                    BrushShape.Square -> drawLine(
                        color = stroke.color.copy(alpha = stroke.alpha),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 12f
                    )

                    BrushShape.Circle -> drawCircle(
                        color = stroke.color.copy(alpha = stroke.alpha),
                        radius = 6f,
                        center = points[i]
                    )

                    BrushShape.Triangle -> {
                        val halfSize = 6f

                        val trianglePath = Path().apply {
                            moveTo(points[i].x, points[i].y - halfSize) // Top vertex
                            lineTo(points[i].x - halfSize, points[i].y + halfSize) // Bottom left
                            lineTo(points[i].x + halfSize, points[i].y + halfSize) // Bottom right
                            close() // Connect back to top
                        }

                        drawPath(
                            path = trianglePath,
                            color = stroke.color.copy(alpha = stroke.alpha)
                        )
                    }
                }

            }
        }
    }
}

// ------------------------------------------------------
// Color Picker Composable
// ------------------------------------------------------
// Code modified by Collin Giles
// Source: https://www.geeksforgeeks.org/kotlin/color-picker-in-android-using-jetpack-compose/#
@Composable
fun ColorPicker(onColorSelected: (Color) -> Unit) {
    val controller = rememberColorPickerController()

    Column(modifier = Modifier.padding(5.dp)) {
        AlphaTile(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp)),
            controller = controller
        )
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(10.dp),
            controller = controller,
            onColorChanged = { colorEnvelope -> onColorSelected(colorEnvelope.color) }
        )
        AlphaSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
            tileOddColor = Color.White,
            tileEvenColor = Color.Black
        )
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller
        )
    }
}
