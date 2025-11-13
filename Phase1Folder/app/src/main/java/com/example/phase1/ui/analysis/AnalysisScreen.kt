package com.example.phase1.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.phase1.vm.DrawingViewModel

@Composable
fun AnalysisScreen(
    navController: NavController,
    viewModel: DrawingViewModel
) {
    val state = viewModel.visionState
    val bitmap = viewModel.background?.asImageBitmap()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Analysis") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            tint = Color.Black,
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
                    labels = state.labels.map { "${it.description} — ${(it.score ?: 0f) * 100}%" },
                    objects = state.objects,
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
    objects: List<com.example.phase1.data.repository.vision.VisionObject>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("AI Results", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // ⭐ IMAGE WITH BOUNDING BOXES
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            contentAlignment = Alignment.Center
        ) {
            ImageWithBoundingBoxes(bitmap, objects)
        }

        Spacer(Modifier.height(24.dp))

        // ⭐ LABEL LIST
        Text("Detected Labels", style = MaterialTheme.typography.titleMedium)
        labels.forEach { label ->
            Text("• $label")
        }

        Spacer(Modifier.height(24.dp))

        // ⭐ OBJECT LIST
        Text("Detected Objects", style = MaterialTheme.typography.titleMedium)
        objects.forEach { obj ->
            Text("• ${obj.name} — ${(obj.score ?: 0f) * 100}%")
        }
    }
}

@Composable
private fun ImageWithBoundingBoxes(
    bitmap: ImageBitmap,
    objects: List<com.example.phase1.data.repository.vision.VisionObject>
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
                    size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                    color = Color.Red,
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}
