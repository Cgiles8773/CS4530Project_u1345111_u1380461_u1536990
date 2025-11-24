/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file implements the SplashScreen composable, which shows
 * a simple animated splash screen and navigates to the main
 * drawing screen after a short delay.
 */

package com.example.phase1.ui.SplashScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SplashScreen(navController: NavController) {

    // Trigger navigation after 1 second
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000) // 1 second
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true } // remove splash from back stack
        }
    }

    // UI of splash screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6850A5)), // primary
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Demo Splash Screen",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
