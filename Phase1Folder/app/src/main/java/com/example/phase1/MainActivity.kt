package com.example.phase1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.ViewModel
import com.example.phase1.ui.theme.Phase1Theme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.addPathNodes

// ------------------------------------------------------
// View Model
// ------------------------------------------------------
class DrawingViewModel : ViewModel() {

}
// ------------------------------------------------------
// View
// ------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Phase1Theme {
                var showSettings by remember { mutableStateOf(false) }

                Scaffold(
                    floatingActionButton = {
                        if(!showSettings) {
                            FloatingActionButton(onClick = { showSettings = true }) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Brush Settings"
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding), // respect system bars
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing area
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize(0.95f)
                                .border(5.dp, Color.Black)
                        ) {
                            DrawingCanvas()
                        }
                        // Settings window
                        if (showSettings) {
                            SettingsWindow(onDismiss = { showSettings = false })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsWindow(onDismiss: () -> Unit) {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)   // 90% of screen width
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
                // Title
                Text(
                    "Brush Settings",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )

                // Color picker placeholder
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
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it},
                        modifier = Modifier.weight(1f),
                        valueRange = 0f..1f
                    )
                }

                // Brush shape buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.size(64.dp)
                    ) {
                        //Square
                        Text("Square")
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.size(64.dp)
                    ) {
                        //Circle
                        Text("Circle")
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.size(64.dp)
                    ) {
                        //Triangle
                        Text("Triangle")
                    }
                }

                // Close button
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

//Composable function that uses a Canvas composable to draw on the screen
@Composable
fun DrawingCanvas() {
    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf(listOf<Offset>()) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clipToBounds()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Convert to local canvas coordinates
                        val local = offset
                        currentStroke = listOf(local)
                        strokes = strokes + listOf(currentStroke)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        // Convert to local canvas coordinates
                        val local = change.position
                        currentStroke = currentStroke + local
                        strokes = strokes.dropLast(1) + listOf(currentStroke)
                    },
                    onDragEnd = {
                        currentStroke = emptyList()
                    }
                )
            }
    ) {
        // Draw all completed strokes
        strokes.forEach { stroke ->
            for (i in 0 until stroke.size - 1) {
                drawLine(
                    color = Color.Black,
                    start = stroke[i],
                    end = stroke[i + 1],
                    strokeWidth = 8f
                )
            }
        }
    }
}
