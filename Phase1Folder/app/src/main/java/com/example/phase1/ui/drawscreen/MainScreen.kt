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

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.border
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.phase1.ui.drawscreen.SaveWindow
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
fun MainScreen(navController: NavController, viewModel: DrawingViewModel, filepath: String?) {
    val canvasSize = remember { mutableStateOf(IntSize.Zero) }
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    LaunchedEffect(orientation, canvasSize.value) {
        viewModel.onOrientationChanged(orientation, canvasSize.value)
    }

    Scaffold(
    ) { innerPadding ->
        when(configuration.orientation) { Configuration.ORIENTATION_PORTRAIT ->
        {
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
                            //TODO
                            .fillMaxWidth(.90f)
                            .fillMaxHeight(.9f)
                            .border(5.dp, Color.Black)
                            .onGloballyPositioned{coordinates -> canvasSize.value = coordinates.size }
                            .padding(3.dp)
                    ) {
                        DrawingCanvas(viewModel)
                    }
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceEvenly)
                    {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,)
                        {
                            Button(
                                onClick = { viewModel.toggleSave() }) { Text("Save") }
                            Button(
                                onClick = { navController.navigate("home") }) { Text("Home") }
                            Button(
                                onClick = { viewModel.clearCanvas() }) { Text("Clear") }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,)
                        {
                            Button(
                                onClick = { viewModel.toggleSettings() }) { Text("Settings") }
                            Button(
                                onClick = {
                                    val bmp = viewModel.getBitmap()
                                    if (bmp != null) {
                                        val tempPath = viewModel.saveTempBitmap(bmp)
                                        if (tempPath != null) {
                                            val encoded = URLEncoder.encode(tempPath, StandardCharsets.UTF_8.toString())
                                            navController.navigate("analysis/$encoded")
                                        } else {
                                            Log.d("AI", "Failed to save temp bitmap")
                                        }
                                    } else {
                                        Log.d("AI", "No bitmap to analyze")
                                    }
                                }
                            ) {
                                Text("Analyze")
                            }
                        }
                    }
                    if (viewModel.showSettings) {
                        SettingsWindow(
                            viewModel = viewModel,
                            onDismiss = { viewModel.toggleSettings() }
                        )
                    }
                    if(viewModel.showSave) {
                        SaveWindow(
                            viewModel = viewModel,
                            onDismiss = { viewModel.toggleSave() },
                            canvasSize = canvasSize.value.height
                        )
                    }
                }

            }
        }
            Configuration.ORIENTATION_LANDSCAPE ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                )
                {
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically)
                    {
                        Box(
                            modifier = Modifier
                                //TODO
                                .fillMaxWidth(.88f)
                                .fillMaxHeight()
                                .border(5.dp, Color.Black)
                                .onGloballyPositioned{coordinates -> canvasSize.value = coordinates.size }
                                .padding(3.dp)
                        ) {
                            DrawingCanvas(viewModel)
                        }
                        ////////////////////////////////////////////////
                        Column(modifier = Modifier.fillMaxHeight().padding(start = 4.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally)
                        {
                            val buttonWidth = 100.dp
                            Button(modifier = Modifier.width(buttonWidth),
                                onClick = { viewModel.toggleSave() }) { Text("Save") }
                            Button(modifier = Modifier.width(buttonWidth),
                                onClick = { navController.navigate("home") }) { Text("Home") }
                            Button(modifier = Modifier.width(buttonWidth),
                                onClick = { viewModel.clearCanvas() }) { Text("Clear") }
                            Button(modifier = Modifier.width(buttonWidth),
                                onClick = { viewModel.toggleSettings() }) { Text("Settings") }
                            Button(
                                modifier = Modifier.width(buttonWidth),
                                onClick = {
                                    val bmp = viewModel.getBitmap()
                                    Log.d("AI", "Bitmap from viewModel = ${bmp != null}")

                                    if (bmp != null) {
                                        val savedPath = viewModel.saveTempBitmap(bmp)
                                        Log.d("AI", "Saved temp path = $savedPath")

                                        val encoded = URLEncoder.encode(savedPath, StandardCharsets.UTF_8.toString())

                                        navController.navigate("analysis/$encoded")
                                    } else {
                                        Log.d("AI", "No background image to analyze")
                                    }
                                }
                            ) { Text("Analyze") }

                        }
                        ////////////////////////////////////////////////
                        if (viewModel.showSettings) {
                            SettingsWindow(
                                viewModel = viewModel,
                                onDismiss = { viewModel.toggleSettings() }
                            )
                        }
                        if(viewModel.showSave) {
                            SaveWindow(
                                viewModel = viewModel,
                                onDismiss = { viewModel.toggleSave() },
                                canvasSize = canvasSize.value.height
                            )
                        }
                    }
                }
            }
        }
    }
}
