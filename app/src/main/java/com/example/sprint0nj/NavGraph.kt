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
    startDestination: String = "login"
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("library") {
            LibraryScreen(navController)
        }

        composable("friends") {
            FriendsScreen(navController)
        }

        composable("workout/{playlistId}") { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")
            if (playlistId.isNullOrEmpty()) {
                Log.d("NavGraph", "Missing playlistId in navigation args")
            } else {
                WorkoutScreen(navController, playlistId)
            }
        }

        composable("tutorial/{workoutId}") { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId")
            if (workoutId == null) {
                Log.e("NavGraph", "Missing workoutId for TutorialScreen")
            } else {
                TutorialScreen(navController, workoutId)
            }
        }
    }
}
