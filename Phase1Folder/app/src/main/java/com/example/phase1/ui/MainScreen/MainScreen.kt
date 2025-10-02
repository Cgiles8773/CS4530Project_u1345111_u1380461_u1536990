package com.example.phase1.ui.MainScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.phase1.vm.DrawingViewModel

// ------------------------------------------------------
// Main Screen Composable (View layer)
// ------------------------------------------------------
@Composable
fun MainScreen(navController: NavController, viewModel: DrawingViewModel) {
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