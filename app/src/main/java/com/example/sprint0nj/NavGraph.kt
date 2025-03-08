package com.example.sprint0nj

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
        composable("workout") {

            WorkoutScreen(navController) // <-- Workout Screen
        }
    }
}