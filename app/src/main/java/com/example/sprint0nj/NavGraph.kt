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
    startDestination: String = "library" // if you want home to appear first
) {
    NavHost(navController = navController, startDestination = startDestination) {
        /*
        composable("home") {
            HomeScreen(navController) // <-- Home screen
        }
        */

        composable("library") {
            LibraryScreen(navController) // <-- Library screen
        }
        composable("workout/{playlistId}") { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")
            if (playlistId.isNullOrEmpty()) {
                Log.d("NavGraph", "Missing playlistId in navigation args")
                return@composable
            }
            WorkoutScreen(navController, playlistId) // <-- Workout Screen
        }
    }
}