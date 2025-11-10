/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file implements the ColorPicker composable, which provides
 * UI controls for selecting pen color, brightness, and alpha transparency
 * using the skydoves color picker library.
 */

package com.example.phase1.ui.MainScreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController


// ------------------------------------------------------
// Color Picker Composable
// ------------------------------------------------------
// Code modified by Collin Giles
// Source: https://www.geeksforgeeks.org/kotlin/color-picker-in-android-using-jetpack-compose/#
@Composable
fun ColorPicker(onColorSelected: (Color) -> Unit, initialColor: Color) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    val controller = rememberColorPickerController()
    controller.debounceDuration = 100L
    LaunchedEffect(initialColor) {
        controller.selectByColor(initialColor, true)
    }
    when(orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Column(modifier = Modifier.padding(5.dp)) {
                AlphaTile(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                        .padding(10.dp),

                    controller = controller,
                    onColorChanged = { colorEnvelope -> onColorSelected(colorEnvelope.color) }
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(30.dp),
                    controller = controller,
                    tileOddColor = Color.White,
                    tileEvenColor = Color.Black
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(30.dp),
                    controller = controller
                )
            }
        }
    }
    when(orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(modifier = Modifier.padding(5.dp)) {
                AlphaTile(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope -> onColorSelected(colorEnvelope.color) }
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                    tileOddColor = Color.White,
                    tileEvenColor = Color.Black
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller
                )
            }
        }
    }
}
