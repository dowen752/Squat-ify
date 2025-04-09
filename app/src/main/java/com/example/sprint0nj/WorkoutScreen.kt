package com.example.sprint0nj

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast // "Toast" is an Android API used to display the short confirmation messages after clicking the buttons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.sprint0nj.data.FirestoreRepository
import com.example.sprint0nj.data.Classes.Playlist
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
// Import the separate MoreOptionsMenu composable from its own file.
import com.example.sprint0nj.MoreOptionsMenu
import com.example.sprint0nj.data.Classes.Workout
import kotlinx.coroutines.launch


@Composable
fun WorkoutScreen(navController: NavController, playlistId: String) {
    // This captures the current context which is used in the callbacks for popup
    val context = LocalContext.current
    val firestoreRepository = remember { FirestoreRepository()}
    val scope = rememberCoroutineScope()
    val playlist = remember { mutableStateOf<Playlist?>(null) }

    // State to hold the workout entry for editing; if null, we're in add mode
    var workoutToEdit by remember { mutableStateOf<WorkoutEntry?>(null) }

    val localFetchPlaylist = {
        scope.launch{
            val updated = firestoreRepository.fetchPlaylist(playlistId)
            playlist.value = updated
        }
    }

    // State for the list of available workouts from Fire store.
    val workoutsList = remember { mutableStateOf<List<Workout>>(emptyList()) }

    // State to control the visibility of the workout selection dialog.
    var showWorkoutSelectionDialog by remember { mutableStateOf(false) }

    // Fetch both the playlist and workouts from Fire store.
    LaunchedEffect(playlistId) {
        Log.d("WorkoutScreen", " Attempting to fetch playlist with ID: $playlistId")
        localFetchPlaylist()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Green background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shifted this down
        Spacer(modifier = Modifier.height(80.dp))

        if (playlist.value == null) { // Loading if playlist hasn't been fetched yet
            Text(text = "Loading...", fontSize = 20.sp, color = Color.Black)
            return@Column
        }
        // Top row with "Playlist #1" and a plus button on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = playlist.value?.name ?: "Unnamed Playlist",
                fontSize = 24.sp,
                color = Color.White
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                // Use PlusButtonWithMenu without referencing workouts state.
                PlusButtonWithMenu(
                    menuOptions = listOf(
                        MenuOption("Add Workout") {
                            // This callback is handled here:
                            showWorkoutSelectionDialog = true
                            // debug Toast:
                            //Toast.makeText(context, "Add Workout callback triggered", Toast.LENGTH_SHORT).show()
                        },
                        MenuOption("Import Workout") {
                            Toast.makeText(context, "Import Workout clicked", Toast.LENGTH_SHORT).show()
                        }
                    ),
                    onPlaylistAdded = {

                    }
                )
            }
        }

        // For list of workouts:

        // Display the WorkoutSelectionDialog if the state is true.
        if (showWorkoutSelectionDialog) {
            WorkoutSelectionDialog(
                playlist = playlist.value!!,
                initialWorkout = workoutToEdit, // Pass the data if present—dialog will be in edit mode.
                onDismiss = {
                    showWorkoutSelectionDialog = false
                    workoutToEdit = null
                },
                onConfirm = { workoutEntry ->
                    // Here you update your UI, and later add Firebase logic.
                    Toast.makeText(
                        context,
                        "Workout updated: ${workoutEntry.name} with ${workoutEntry.reps} reps and ${workoutEntry.sets} sets",
                        Toast.LENGTH_SHORT
                    ).show()
                    showWorkoutSelectionDialog = false
                    workoutToEdit = null
                }
            )
            localFetchPlaylist()
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f) // Takes up available vertical space to allow scrolling
                .fillMaxWidth()
        ) {
            // Example list of workouts with #Reps and #Sets
            // In a real app, you might replace this with dynamic data
            items(playlist.value!!.workouts) { workout ->
                val context = LocalContext.current

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFF212121), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left placeholder icon/box
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Gray, RoundedCornerShape(4.dp))
                    )

                    // Workout details
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(start = 16.dp)
                    ) {
                        Text(text = workout.title, fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "# Reps: ${workout.reps ?: "-"}",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "# Sets: ${workout.sets ?: "-"}",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }

                    MoreOptionsMenu(
                        onShare = {
                            Toast.makeText(
                                context,
                                "Share workout: ${workout.title}",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onRemove = {
                            firestoreRepository.removeWorkout(
                                playlistId = playlistId,
                                workoutId = workout.id,
                                onSuccess = {
                                    localFetchPlaylist()
                                    Toast.makeText(
                                        context,
                                        "Removed: ${workout.title}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        },
                        onEdit = {
                            workoutToEdit = WorkoutEntry(
                                name = workout.title,
                                reps = workout.reps ?: 0,
                                sets = workout.sets ?: 0
                            )
                            showWorkoutSelectionDialog = true
                        },
                        onTutorial = {
                            navController.navigate("tutorial")
                        }
                    )
                }
            }
        }


            Spacer(modifier = Modifier.height(16.dp))

        // Example bottom navigation row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { navController.navigate("library") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
            ) {
                Text("Library", color = Color.White)
            }
        }
    }
}
