/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * Navigation graph for the application.
 * Handles routing between splash, main, home, and analysis screens.
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
import com.example.phase1.ui.analysis.AnalysisScreen
import com.example.phase1.ui.communityscreen.CommunityScreen
import com.example.phase1.ui.login.LoginScreen
import com.example.phase1.vm.DrawingViewModel
import com.example.phase1.vm.HomeViewModel
import com.example.phase1.vm.LoginViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // Splash screen
        composable("splash") {
            SplashScreen(navController)
        }

        // Main screen with file path
        composable(
            route = "main/{filePath}",
            arguments = listOf(
                navArgument("filePath") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val viewModel: DrawingViewModel = hiltViewModel()

            val decodedPath = entry.arguments
                ?.getString("filePath")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }

            viewModel.loadDrawing(decodedPath)
            MainScreen(navController, viewModel, decodedPath)
        }

        // Main screen without a file
        composable("main") {
            val viewModel: DrawingViewModel = hiltViewModel()
            MainScreen(navController, viewModel, null)
        }

        // Home screen
        composable("home") {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(navController, homeViewModel)
        }

        // Analysis screen
        composable(
            route = "analysis/{filePath}",
            arguments = listOf(
                navArgument("filePath") { type = NavType.StringType }
            )
        ) { entry ->
            val decoded = URLDecoder.decode(
                entry.arguments!!.getString("filePath")!!,
                StandardCharsets.UTF_8.toString()
            )
            AnalysisScreen(navController, decoded)
        }

        // Login screen
        //TODO: Remove after done testing
        composable(
            route = "login"
        )
        {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(navController, loginViewModel)
        }

        // Community Page
        composable(
            route = "community"
        )
        {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val drawingViewModel: DrawingViewModel = hiltViewModel()
            CommunityScreen(homeViewModel, drawingViewModel, navController)
        }
    }
}
