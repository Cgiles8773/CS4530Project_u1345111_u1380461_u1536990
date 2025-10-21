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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.phase1.ui.MainScreen.MainScreen
import com.example.phase1.ui.SplashScreen.SplashScreen
import com.example.phase1.ui.homescreen.HomeScreen
import com.example.phase1.vm.DrawingViewModel
import com.example.phase1.vm.HomeViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable(
            route = "main/{filePath}",
            arguments = listOf(
                navArgument("filePath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: DrawingViewModel = hiltViewModel()

            // Decode the filePath (since it may contain slashes)
            val decodedPath = backStackEntry.arguments?.getString("filePath")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            }
            viewModel.loadDrawing(decodedPath)
            MainScreen(navController, viewModel, decodedPath)
        }

        composable("main") {
            val viewModel: DrawingViewModel = hiltViewModel()
            MainScreen(navController, viewModel, null)
        }

        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(navController, viewModel)
        }
    }
}