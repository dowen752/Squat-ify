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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items



@Composable
fun WorkoutScreen(navController: NavController, playlistId: String) {
    // This captures the current context which is used in the callbacks for popup
    val context = LocalContext.current
    val firestoreRepository = remember { FirestoreRepository() }
    val scope = rememberCoroutineScope()
    val playlist = remember { mutableStateOf<Playlist?>(null) }

    val localFetchPlaylist = {
        scope.launch {
            val updated = firestoreRepository.fetchPlaylist(playlistId)
            // Enforce uniqueness on the fetched playlist as well.
            if (updated != null) {
                updated.workouts = updated.workouts.distinctBy { it.id }.toMutableList()
                Log.d("WorkoutDebug", "Fetched playlist workouts count after distinctBy: ${updated.workouts.size}")
            }
            playlist.value = updated
        }
    }
    /*
    // State for the list of available workouts from Firestore.
    val workoutsList = remember { mutableStateOf<List<Workout>>(emptyList()) }

    // State to control the visibility of the workout selection dialog.
    var showWorkoutSelectionDialog by remember { mutableStateOf(false) }
*/
    // Fetch both the playlist and workouts from Firestore.
    LaunchedEffect(playlistId) {
        Log.d("WorkoutScreen", " Attempting to fetch playlist with ID: $playlistId")
        localFetchPlaylist()
    }

    // state variables for controlling the add/edit dialog.
    var showWorkoutDialog by remember { mutableStateOf(false) }
    var workoutToEdit by remember { mutableStateOf<WorkoutEntry?>(null) }
    var editingWorkoutId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Green background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shifted this down
        Spacer(modifier = Modifier.height(80.dp))

        if (playlist.value == null) { // Loading if playlist hasnt been fetched yet
            Text(text = "Loading...", fontSize = 20.sp, color = Color.Black)
            return@Column
        }
        // Top row: Playlist title and plus button.
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
                            // For adding a workout, clear any editing data.
                            workoutToEdit = null
                            editingWorkoutId = null
                            showWorkoutDialog = true
                        },
                        MenuOption("Import Workout") {
                            Toast.makeText(context, "Import Workout clicked", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                )
            }
        }

        // List existing workouts.
        LazyColumn {
            items(playlist.value!!.workouts, key = { it.id }) { workout ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left placeholder icon.
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Gray, RoundedCornerShape(4.dp))
                    )
                    // Workout details.
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(text = workout.title, fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "# Reps: ${workout.reps ?: "-"}",
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "# Sets: ${workout.sets ?: "-"}",
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }
                    // "More Options" menu.
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
                                },
                                onFailure = {
                                    Toast.makeText(
                                        context,
                                        "Failed to remove: ${workout.title}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        },
                        onEdit = {
                            // Set workoutToEdit with the selected workout's data for editing.
                            workoutToEdit = WorkoutEntry(
                                name = workout.title,
                                reps = workout.reps ?: 0,
                                sets = workout.sets ?: 0
                            )
                            editingWorkoutId = workout.id
                            showWorkoutDialog = true
                        },
                        onTutorial = {
                            navController.navigate("tutorial")
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom navigation row.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { navController.navigate("library") }) {
                Text("Library")
            }
        }
    }

    // Display the dialog for adding/editing a workout.
    if (showWorkoutDialog) {
        WorkoutSelectionDialog(
            playlist = playlist.value!!,
            initialWorkout = workoutToEdit, // For editing, this is non-null; for adding, it's null.
            onDismiss = {
                showWorkoutDialog = false
                workoutToEdit = null
                editingWorkoutId = null
            },
            onConfirm = { updatedWorkout ->
                if (editingWorkoutId != null) {
                    // Edit operation: attempt to update in place.
                    val index = playlist.value?.workouts?.indexOfFirst { it.id == editingWorkoutId } ?: -1
                    if (index != -1) {
                        playlist.value?.workouts?.set(
                            index,
                            Workout(
                                id = editingWorkoutId!!, // Preserve original ID.
                                title = updatedWorkout.name,
                                reps = updatedWorkout.reps,
                                sets = updatedWorkout.sets,
                                description = ""
                            )
                        )
                        Toast.makeText(context, "Workout updated: ${updatedWorkout.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        // Fallback: if not found, add it.
                        playlist.value?.workouts?.add(
                            Workout(
                                id = editingWorkoutId!!,
                                title = updatedWorkout.name,
                                reps = updatedWorkout.reps,
                                sets = updatedWorkout.sets,
                                description = ""
                            )
                        )
                        Toast.makeText(context, "Workout added (unexpected): ${updatedWorkout.name}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Add operation: generate new ID.
                    playlist.value?.workouts?.add(
                        Workout(
                            id = java.util.UUID.randomUUID().toString(),
                            title = updatedWorkout.name,
                            reps = updatedWorkout.reps,
                            sets = updatedWorkout.sets,
                            description = ""
                        )
                    )
                    Toast.makeText(context, "Workout added: ${updatedWorkout.name}", Toast.LENGTH_SHORT).show()
                }

                // Enforce uniqueness locally.
                playlist.value?.workouts = playlist.value?.workouts
                    ?.distinctBy { it.id }
                    ?.toMutableList() ?: mutableListOf()

                Log.d("WorkoutDebug", "Local list before postPlaylist: ${playlist.value?.workouts?.size}")

                firestoreRepository.postPlaylist(playlist.value!!)
                showWorkoutDialog = false
                workoutToEdit = null
                editingWorkoutId = null
                localFetchPlaylist()
            }

        )
    }
}
