package com.example.phase1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
                Box()
                {
                    DrawingCanvas()
                }
                //Scaffold(modifier = Modifier.fillMaxSize())
                //{
                //innerPadding ->
                //DrawingView()
                //}
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
            .pointerInput(Unit) {
                // Capture input
                detectDragGestures(
                    onDragStart = { offset ->
                        currentStroke = listOf(offset)
                        strokes = strokes + listOf(currentStroke)
                    },
                    onDrag = { change, x ->
                        change.consume()
                        currentStroke = currentStroke + change.position
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
