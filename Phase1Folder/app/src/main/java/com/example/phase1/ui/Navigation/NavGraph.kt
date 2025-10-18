/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file defines the navigation graph for the application.
 * It manages navigation between the SplashScreen and MainScreen,
 * and ensures lifecycle-aware usage of the DrawingViewModel.
 */

package com.example.phase1.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.phase1.ui.MainScreen.MainScreen
import com.example.phase1.ui.SplashScreen.SplashScreen
import com.example.phase1.ui.homescreen.HomeScreen
import com.example.phase1.vm.DrawingViewModel
import com.example.phase1.vm.HomeViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("main") {
            val viewModel: DrawingViewModel = hiltViewModel()
            MainScreen(navController, viewModel)
        }

        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(navController, viewModel)
        }
    }
}