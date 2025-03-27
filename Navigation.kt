package com.example.squat_ifyplaylists

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

class SecondTask1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "playlists") {
        composable("playlists") { PlaylistsScreen(navController) }
        composable("create_playlist") { CreatePlaylistScreen(navController) }
        composable("create_workout") { CreateWorkoutScreen(navController) }
    }
}

@Composable
fun CreateWorkoutScreen(navController: NavController) {
    var workoutName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Create New Workout", style = MaterialTheme.typography.headlineSmall)

        TextField(value = workoutName, onValueChange = { workoutName = it }, label = { Text("Workout Name") })
        TextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration") })
        TextField(value = difficulty, onValueChange = { difficulty = it }, label = { Text("Difficulty") })

        Button(onClick = {
            if (workoutName.isNotEmpty() && duration.isNotEmpty() && difficulty.isNotEmpty()) {
                saveWorkoutToFirebase(workoutName, duration, difficulty)
                navController.popBackStack()
            }
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Save Workout")
        }
    }
}

fun saveWorkoutToFirebase(name: String, duration: String, difficulty: String) {
    val db = FirebaseFirestore.getInstance()
    val workout = hashMapOf("name" to name, "duration" to duration, "difficulty" to difficulty)

    db.collection("workouts")
        .add(workout)
        .addOnSuccessListener { println("Workout added!") }
        .addOnFailureListener { e -> println("Error: $e") }
}
