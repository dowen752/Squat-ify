package com.example.sprint0nj

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.ui.text.input.TextFieldValue
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.sprint0nj.data.Classes.Playlist
import com.example.sprint0nj.data.Classes.Workout
import com.example.sprint0nj.data.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.material3.TextButtonDefaults
import androidx.compose.material3.MaterialTheme



// Data class representing a single menu option
// This class holds the title (what is displayed in the menu) and an action (lambda) to execute on click
data class MenuOption(
    val title: String,       // The text displayed for the menu option
    val onClick: () -> Unit  // The action executed when the option is selected
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistNameDialog(
    onDismiss: () -> Unit,      // Called to dismiss the dialog
    onConfirm: (String) -> Unit, // Called with the entered playlist name when confirmed
    onPlaylistAdded: () -> Unit
) {
    val context = LocalContext.current
    var playlistName by remember { mutableStateOf(TextFieldValue("")) }
    val firestoreRepository = remember { FirestoreRepository() }
    var selectedUserId = FirebaseAuth.getInstance().currentUser?.uid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Playlist Name:") },
        text = {
            // BasicTextField to allow the user to type in the playlist name
            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Playlist Name") },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
        },
        confirmButton = {
            // Confirm button returns the entered playlist name
            TextButton(
                onClick = {
                    val playlist = Playlist(
                        id = UUID.randomUUID().toString(),
                        name = playlistName.text
                    )
                    firestoreRepository.postPlaylist(
                        playlist = playlist,
                        userId = selectedUserId!!,
                        onSuccess = {
                            val name = playlist.name
                            onPlaylistAdded()
                        }
                    )

                    onConfirm(playlistName.text)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor   = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            // Cancel Button: Customize its UI similarly to the Confirm Button
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor   = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}


// Data class representing a workout entry with its configuration.
data class WorkoutEntry(
    val name: String,
    val reps: Int,
    val sets: Int
)

///////////////////////////////////////////////////////////////////////////////////

@Composable
fun WorkoutSelectionDialog(
    playlist: Playlist,
    initialWorkout: WorkoutEntry? = null,
    onDismiss: () -> Unit,
    onConfirm: (WorkoutEntry) -> Unit
) {
    var selectedWorkout by remember { mutableStateOf(initialWorkout?.name ?: "") }
    var repsText by remember { mutableStateOf(initialWorkout?.reps?.toString() ?: "") }
    var setsText by remember { mutableStateOf(initialWorkout?.sets?.toString() ?: "") }

    // State for the Target Muscle button
    var selectedTargetMuscle by remember { mutableStateOf("") }
    var isTargetMuscleDropdownExpanded by remember { mutableStateOf(false) }
    // A sample list of available target muscles
    val availableMuscles = remember { mutableStateOf(listOf("Chest", "Back", "Legs", "Biceps", "Triceps", "Shoulders", "Abs")) }


    var isDropdownExpanded by remember { mutableStateOf(false) }
    val availableWorkouts = remember { mutableStateOf<List<Workout>>(emptyList())}
    val firestoreRepository = remember {FirestoreRepository()}
//    val playlist = remember { mutableStateOf<Playlist?>(null) }
    var selectedUserId = "4dz7wUNpKHI0Br9lSg9o" // Will need to be updated to allow for multiple
    var selectedWorkoutObject by remember { mutableStateOf<Workout?>(null) }                       // users instead of hardcoding

    // Filters firestore list for targeted muscle
    val filteredWorkouts by remember(selectedTargetMuscle, availableWorkouts.value) {
        derivedStateOf {
            if (selectedTargetMuscle.isEmpty()) {
                availableWorkouts.value
            } else {
                availableWorkouts.value.filter { it.target.equals(selectedTargetMuscle, ignoreCase = true) }
            }
        }
    }

    LaunchedEffect(Unit){
        availableWorkouts.value = firestoreRepository.fetchWorkouts()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        //title = { Text("Select Workout:") }, // Optional title if needed.
        text = {
            Column {
                // Dropdown for selecting a workout.
                Box {
                    Button(
                        onClick = { isDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))


                    ) {
                        Text(
                            text = selectedWorkout.ifEmpty { "Select workout" },
                            fontSize = 16.sp

                        )
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        filteredWorkouts.forEach { workout ->
                            DropdownMenuItem(
                                text = { Text(workout.title) },
                                onClick = {
                                    selectedWorkout = workout.title
                                    selectedWorkoutObject = workout
                                    isDropdownExpanded = false
                                    repsText = workout.reps?.toString() ?: "-"
                                    setsText = workout.sets?.toString() ?: "-"
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // "Target Muscle" Button and Dropdown

                Box {
                    Button(
                        onClick = { isTargetMuscleDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
                    ) {
                        Text(
                            text = selectedTargetMuscle.ifEmpty { "Target Muscle" },
                            fontSize = 16.sp
                        )
                    }
                    DropdownMenu(
                        expanded = isTargetMuscleDropdownExpanded,
                        onDismissRequest = { isTargetMuscleDropdownExpanded = false }
                    ) {
                        availableMuscles.value.forEach { muscle ->
                            DropdownMenuItem(
                                text = { Text(muscle) },
                                onClick = {
                                    selectedTargetMuscle = muscle
                                    isTargetMuscleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // TextField for entering the number of Reps.
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it },
                    label = { Text("Reps") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // TextField for entering the number of Sets.
                OutlinedTextField(
                    value = setsText,
                    onValueChange = { setsText = it },
                    label = { Text("Sets") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Ensure a workout is selected and convert reps/sets safely to integers.
                    if (selectedWorkout.isNotEmpty()) {
                        val reps = repsText.toIntOrNull() ?: 0
                        val sets = setsText.toIntOrNull() ?: 0

                        // Create a WorkoutEntry from the inputs
                        val workoutEntry = WorkoutEntry(selectedWorkout, reps, sets) // maybe we could include target muscle here

                        if (initialWorkout == null) {
                            // Add operation: add a new workout
                            playlist.workouts.add(
                                Workout(
                                    id = selectedWorkoutObject?.id ?: "0000",
                                    title = selectedWorkout,
                                    reps = reps,
                                    sets = sets,
                                    description = ""
                                )
                            )
                        } else {
                            // Edit operation: update the existing workout

                            // using the initialWorkout.name as a placeholder for identifying the workout
                            val index = playlist.workouts.indexOfFirst { it.title == initialWorkout.name }
                            if (index != -1) {
                                // update the found workout while keeping its ID
                                playlist.workouts[index] = Workout(
                                    id = playlist.workouts[index].id,
                                    title = selectedWorkout,
                                    reps = reps,
                                    sets = sets,
                                    description = ""
                                )
                            } else {
                                // if not found, add it
                                playlist.workouts.add(
                                    Workout(
                                        id = UUID.randomUUID().toString(),
                                        title = selectedWorkout,
                                        reps = reps,
                                        sets = sets,
                                        description = ""
                                    )
                                )
                            }
                        }

                        firestoreRepository.postPlaylist(
                            playlist = playlist,
                            userId = selectedUserId
                        )
                        onConfirm(workoutEntry)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

///////////////////////////////////////////////////////////////////////////////////

// Reusable composable for the plus button with a popup (dropdown) menu
// This component can be used in multiple screens by passing different lists of MenuOption items
@Composable
fun PlusButtonWithMenu(
    menuOptions: List<MenuOption>,
    onPlaylistAdded: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        Button(
            onClick = {
                if (menuOptions.size == 1) {
                    menuOptions[0].onClick()
                } else {
                    // otherwise show the dropdown
                    menuExpanded = true
                }
            },
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            if (menuOptions.size > 1) {
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    offset = DpOffset(0.dp, 0.dp)
                ) {
                    menuOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.title) },
                            onClick = {
                                menuExpanded = false
                                option.onClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

