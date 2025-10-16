/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file implements the MainScreen composable, which serves
 * as the primary drawing screen. It displays the DrawingCanvas,
 * conditionally shows the SettingsWindow, and includes a floating
 * action button for toggling brush settings.
 */

package com.example.phase1.ui.MainScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.phase1.vm.DrawingViewModel


/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file implements the MainScreen composable, which serves
 * as the primary drawing screen. It displays the DrawingCanvas,
 * conditionally shows the SettingsWindow, and includes a floating
 * action button for toggling brush settings.
 */

// ------------------------------------------------------
// Main Screen Composable (View layer)
// ------------------------------------------------------
@Composable
fun MainScreen(navController: NavController, viewModel: DrawingViewModel) {
    val canvasSize = remember { mutableStateOf(IntSize.Zero) }
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
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
            {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize(0.95f)
                        .border(5.dp, Color.Black)
                        .onGloballyPositioned{coordinates -> canvasSize.value = coordinates.size }
                ) {
                    DrawingCanvas(viewModel)
                }
                Button(onClick = { viewModel.clearCanvas() }) { Text("Clear") }
                Button(onClick = { viewModel.saveDrawing("placeholder", canvasSize.value.height, canvasSize.value.width)}) { Text("Save") }
                if (viewModel.showSettings) {
                    SettingsWindow(
                        viewModel = viewModel,
                        onDismiss = { viewModel.toggleSettings() }
                    )
                }
            }
        }
    }
}