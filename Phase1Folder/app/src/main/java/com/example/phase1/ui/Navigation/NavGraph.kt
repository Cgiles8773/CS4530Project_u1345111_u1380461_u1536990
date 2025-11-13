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

        // MAIN with filePath
        composable(
            route = "main/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val vm: DrawingViewModel = hiltViewModel()
            val decodedPath = backStackEntry.arguments?.getString("filePath")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            }
            vm.loadDrawing(decodedPath)
            MainScreen(navController, vm, decodedPath)
        }

        // MAIN blank
        composable("main") {
            val vm: DrawingViewModel = hiltViewModel()
            MainScreen(navController, vm, null)
        }

        // HOME
        composable("home") {
            val vm: HomeViewModel = hiltViewModel()
            HomeScreen(navController, vm)
        }

        // ⭐ ANALYSIS (correct version)
        composable(
            route = "analysis/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val decoded = URLDecoder.decode(
                backStackEntry.arguments!!.getString("filePath")!!,
                StandardCharsets.UTF_8.toString()
            )
            AnalysisScreen(navController, decoded)
        }
    }
}
