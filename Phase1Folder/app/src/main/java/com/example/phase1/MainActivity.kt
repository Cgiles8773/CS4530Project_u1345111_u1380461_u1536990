package com.example.phase1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phase1.ui.theme.Phase1Theme

// ------------------------------------------------------
// ViewModel (business + UI state)
// ------------------------------------------------------
class DrawingViewModel : ViewModel() {
    // Drawing state
    var strokes by mutableStateOf(listOf<List<Offset>>())
        private set
    private var currentStroke: List<Offset> = emptyList()

    // UI state
    var showSettings by mutableStateOf(false)
        private set

    var updateOpacity by mutableFloatStateOf(0.5f)
        private set

    fun startStroke(offset: Offset) {
        currentStroke = listOf(offset)
        strokes = strokes + listOf(currentStroke)
    }

    fun addPointToStroke(offset: Offset) {
        currentStroke = currentStroke + offset
        strokes = strokes.dropLast(1) + listOf(currentStroke)
    }

    fun endStroke() {
        currentStroke = emptyList()
    }

    fun toggleSettings() {
        showSettings = !showSettings
    }

    fun setOpacity(value: Float) {
        updateOpacity = value
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
            // Drawing area
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize(0.95f)
                    .border(5.dp, Color.Black)
            ) {
                DrawingCanvas(viewModel)
            }
            // Settings window
            if (viewModel.showSettings) {
                SettingsWindow(
                    opacity = viewModel.updateOpacity,
                    onOpacityChange = { viewModel.setOpacity(it) },
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
fun SettingsWindow(
    opacity: Float,
    onOpacityChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
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

                // Placeholder color picker
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Color Picker")
                }

                // Opacity slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Opacity", modifier = Modifier.width(80.dp))
                    Slider(
                        value = opacity,
                        onValueChange = onOpacityChange,
                        modifier = Modifier.weight(1f),
                        valueRange = 0f..1f
                    )
                }

                // Brush shape buttons (not yet wired to VM)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {}, modifier = Modifier.size(64.dp)) { Text("Square") }
                    Button(onClick = {}, modifier = Modifier.size(64.dp)) { Text("Circle") }
                    Button(onClick = {}, modifier = Modifier.size(64.dp)) { Text("Triangle") }
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
        // Draw all strokes from ViewModel
        viewModel.strokes.forEach { stroke ->
            for (i in 0 until stroke.size - 1) {
                drawLine(
                    color = Color.Black.copy(alpha = viewModel.updateOpacity),
                    start = stroke[i],
                    end = stroke[i + 1],
                    strokeWidth = 8f
                )
            }
        }
    }
}
