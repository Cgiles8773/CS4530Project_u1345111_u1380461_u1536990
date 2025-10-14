
/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file implements the DrawingCanvas composable, which handles
 * user touch input for drawing. It delegates stroke management to
 * the DrawingViewModel and renders strokes in different shapes
 * (circle, square, triangle).
 */

package com.example.phase1.ui.MainScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import com.example.phase1.vm.DrawingViewModel

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
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        // Start and end a stroke immediately for a single tap
                        viewModel.startStroke(offset)
                        viewModel.endStroke()
                    }
                )
            }
    ) {
        viewModel.strokes.forEach { stroke ->
            val points = stroke.points

            if (points.size == 1) {
                // Handle single-tap strokes
                when (stroke.shape) {
                    DrawingViewModel.BrushShape.Square -> drawRect(
                        color = stroke.color.copy(alpha = stroke.alpha),
                        topLeft = points[0] - Offset(6f, 6f),
                        size = androidx.compose.ui.geometry.Size(stroke.size, stroke.size)
                    )

                    DrawingViewModel.BrushShape.Circle -> drawCircle(
                        color = stroke.color.copy(alpha = stroke.alpha),
                        radius = stroke.size/2,
                        center = points[0]
                    )

                    DrawingViewModel.BrushShape.Triangle -> {
                        val halfSize = stroke.size/2
                        val trianglePath = Path().apply {
                            moveTo(points[0].x, points[0].y - halfSize)
                            lineTo(points[0].x - halfSize, points[0].y + halfSize)
                            lineTo(points[0].x + halfSize, points[0].y + halfSize)
                            close()
                        }
                        drawPath(
                            path = trianglePath,
                            color = stroke.color.copy(alpha = stroke.alpha)
                        )
                    }
                }
            } else {
                // Handle normal multi-point strokes
                for (i in 0 until points.size - 1) {
                    when (stroke.shape) {
                        DrawingViewModel.BrushShape.Square -> drawLine(
                            color = stroke.color.copy(alpha = stroke.alpha),
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 12f
                        )

                        DrawingViewModel.BrushShape.Circle -> drawCircle(
                            color = stroke.color.copy(alpha = stroke.alpha),
                            radius = 6f,
                            center = points[i]
                        )

                        DrawingViewModel.BrushShape.Triangle -> {
                            val halfSize = 6f
                            val trianglePath = Path().apply {
                                moveTo(points[i].x, points[i].y - halfSize)
                                lineTo(points[i].x - halfSize, points[i].y + halfSize)
                                lineTo(points[i].x + halfSize, points[i].y + halfSize)
                                close()
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
}