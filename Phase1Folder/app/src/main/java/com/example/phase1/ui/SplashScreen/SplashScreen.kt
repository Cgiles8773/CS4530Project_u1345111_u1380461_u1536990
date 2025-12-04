/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file implements the SplashScreen composable, which shows
 * a simple animated splash screen and navigates to the main
 * drawing screen after a short delay.
 */

package com.example.phase1.ui.SplashScreen

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.phase1.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun SplashScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    // Trigger navigation after animation finishes
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1800) // 2.05s
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true } // remove splash from back stack
        }
    }

    // UI of splash screen
    if (isPortrait) {
        PortraitAnimation()
    }
    else {
        LandscapeAnimation()
    }

}

@Composable
fun LandscapeAnimation() {
    val config = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { config.screenWidthDp.dp.toPx() }
    val travelPx = screenWidthPx * 0.50f  // travel 50% of screen width

    // animate horizontal offset only (ball stays centered vertically)
    val offsetX = remember { Animatable(0f) }

    // animate the scale (1f = normal size)
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // 1) Ball moves rightward
        offsetX.animateTo(
            targetValue = travelPx,
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseOutBack
            )
        )

        // Brief pause, then expand the ball
        delay(50)
        scale.animateTo(
            targetValue = 12f,
            animationSpec = tween(
                durationMillis = 500,
                easing = EaseIn
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
        contentAlignment = Alignment.CenterStart // align left
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.toInt(), 0) } // horizontal movement
                .size(100.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    transformOrigin = TransformOrigin.Center
                }
                .background(OnPrimary, CircleShape)
        )
    }
}

@Composable
fun PortraitAnimation() {
    val config = LocalConfiguration.current
    val density = LocalDensity.current

    val screenHeightPx = with(density) { config.screenHeightDp.dp.toPx() }
    val risePx = screenHeightPx * 0.50f

    // animate vertical offset only (ball stays centered horizontally)
    val offsetY = remember { Animatable(0f) }

    // animate the scale (1f = normal size)
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // 1) Ball Rises upward
        offsetY.animateTo(
            targetValue = -risePx,
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseOutBack
            )
        )

        // Brief pause, then expand the ball
        delay(50)
        scale.animateTo(
            targetValue = 12f,
            animationSpec = tween(
                durationMillis = 500,
                easing = EaseIn
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(0, offsetY.value.toInt()) }
                .size(100.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    transformOrigin = TransformOrigin.Center
                }
                .background(OnPrimary, CircleShape)
        )
    }
}
