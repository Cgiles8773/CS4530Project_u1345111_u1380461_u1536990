/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file implements the SettingsWindow composable, which provides
 * a dialog UI for customizing brush settings. Users can select brush
 * color (via the ColorPicker) and brush shape (square, circle, triangle).
 * The dialog also includes a close button to dismiss the settings window.
 */

package com.example.phase1.ui.MainScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.phase1.model.BrushShape
import com.example.phase1.vm.DrawingViewModel
import kotlin.math.floor
import kotlin.math.sqrt
import com.example.phase1.ui.theme.Purple40

/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file implements the SettingsWindow composable, which provides
 * a dialog UI for customizing brush settings. Users can select brush
 * color (via the ColorPicker) and brush shape (square, circle, triangle).
 * The dialog also includes a close button to dismiss the settings window.
 */


// ------------------------------------------------------
// Settings Window Composable
// ------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsWindow(viewModel: DrawingViewModel, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
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

                ColorPicker(onColorSelected = { viewModel.setBrushColor(it) },
                viewModel.brushColor)
                //val brushSize = viewModel.brushSize
                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.brushSize,
                    onValueChange = { viewModel.setBrushSize(it)},
                    valueRange = 1f..240f,
                    thumb = { Box(Modifier.size(24.dp).background(color = Purple40, shape = CircleShape), contentAlignment = Alignment.Center) { } }
                )
                Text(text = floor(viewModel.brushSize/12).toString())
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