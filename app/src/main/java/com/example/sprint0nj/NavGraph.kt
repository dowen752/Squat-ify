package com.example.sprint0nj

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    // Start the app on the Login screen
    startDestination: String = "login"
) {
    NavHost(navController = navController, startDestination = startDestination) {
        // 1. Login route
        composable("login") {
            LoginScreen(navController)
        }

        // 2. Library route (playlist screen)
        composable("library") {
            LibraryScreen(navController) // <-- Library screen
        }

        // 3. Workout route
        composable("workout/{playlistId}") { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")
            if (playlistId.isNullOrEmpty()) {
                Log.d("NavGraph", "Missing playlistId in navigation args")
                return@composable
            }
            WorkoutScreen(navController, playlistId) // <-- Workout Screen
        }
        // 4. Tutorial route
        // New tutorial route
        composable("tutorial") {
            TutorialScreen(navController)
        }
    }
}
