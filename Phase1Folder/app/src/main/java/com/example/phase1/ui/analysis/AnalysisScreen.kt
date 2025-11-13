package com.example.phase1.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.phase1.vm.DrawingViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.phase1.data.repository.vision.VisionObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    navController: NavController,
    filePath: String
) {
    val viewModel: DrawingViewModel = hiltViewModel()

    // Load bitmap from given file path
    val bitmap: ImageBitmap? = remember(filePath) {
        viewModel.loadBitmapFromPath(filePath)?.asImageBitmap()
    }

    val state = viewModel.visionState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Analysis") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${state.errorMessage}")
                }
            }

            bitmap != null -> {
                AnalysisContent(
                    bitmap = bitmap,
                    objects = state.objects,
                    labels = state.labels.map {
                        "${it.description} — ${(it.score ?: 0f) * 100}%"
                    },
                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No image available.")
                }
            }
        }
    }
}

@Composable
private fun AnalysisContent(
    bitmap: ImageBitmap,
    labels: List<String>,
    objects: List<VisionObject>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("AI Results", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // IMAGE + BOXES
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            contentAlignment = Alignment.Center
        ) {
            ImageWithBoundingBoxes(bitmap, objects)
        }

        Spacer(Modifier.height(24.dp))

        // LABEL LIST
        Text("Detected Labels", style = MaterialTheme.typography.titleMedium)
        labels.forEach { Text("• $it") }

        Spacer(Modifier.height(24.dp))

        // OBJECT LIST
        Text("Detected Objects", style = MaterialTheme.typography.titleMedium)
        objects.forEach { obj ->
            Text("• ${obj.name} — ${(obj.score ?: 0f) * 100}%")
        }
    }
}

@Composable
private fun ImageWithBoundingBoxes(
    bitmap: ImageBitmap,
    objects: List<VisionObject>
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            objects.forEach { obj ->
                val vertices = obj.boundingPoly?.normalizedVertices ?: return@forEach
                if (vertices.size < 4) return@forEach

                val xs = vertices.mapNotNull { it.x?.times(w) }
                val ys = vertices.mapNotNull { it.y?.times(h) }

                if (xs.isEmpty() || ys.isEmpty()) return@forEach

                val left = xs.min()
                val right = xs.max()
                val top = ys.min()
                val bottom = ys.max()

                drawRect(
                    topLeft = Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(
                        right - left,
                        bottom - top
                    ),
                    color = Color.Red,
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}
