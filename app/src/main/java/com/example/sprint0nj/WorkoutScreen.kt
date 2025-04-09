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


@Composable
fun WorkoutScreen(navController: NavController, playlistId: String) {
    // This captures the current context which is used in the callbacks for popup
    val context = LocalContext.current
    val firestoreRepository = remember { FirestoreRepository()}
    val scope = rememberCoroutineScope()
    val playlist = remember { mutableStateOf<Playlist?>(null) }

    val localFetchPlaylist = {
        scope.launch{
            val updated = firestoreRepository.fetchPlaylist(playlistId)
            playlist.value = updated
        }
    }

    // 1. State for the list of available workouts from Firestore.
    val workoutsList = remember { mutableStateOf<List<Workout>>(emptyList()) }

    // 2. State to control the visibility of the workout selection dialog.
    var showWorkoutSelectionDialog by remember { mutableStateOf(false) }

    // 3. Fetch both the playlist and workouts from Firestore.
    LaunchedEffect(playlistId) {
        Log.d("WorkoutScreen", " Attempting to fetch playlist with ID: $playlistId")
        localFetchPlaylist()

        // Here is where you fetch the workouts list from Firestore.
        // Uncomment and replace with your actual Firestore query.
        /*
        FirebaseFirestore.getInstance().collection("workouts")
            .get()
            .addOnSuccessListener { snapshot ->
                workoutsList.value = snapshot.documents.mapNotNull { it.getString("name") }
            }
        */
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

        if (playlist.value == null) { // Loading if playlist hasnt been fetched yet
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
                    )
                )
            }
        }

        // For list of workouts:

        // Display the WorkoutSelectionDialog if the state is true.
        if (showWorkoutSelectionDialog) {
            WorkoutSelectionDialog(
                playlist = playlist.value!!,
                onDismiss = { showWorkoutSelectionDialog = false },
                onConfirm = { workoutEntry ->
                    // Process the confirmed workout entry here.
                    Toast.makeText(
                        context,
                        "Workout added: ${workoutEntry.name} with ${workoutEntry.reps} reps and ${workoutEntry.sets} sets",
                        Toast.LENGTH_SHORT
                    ).show()
                    showWorkoutSelectionDialog = false
                }
            )
            localFetchPlaylist()
        }

        // Example list of workouts with #Reps and #Sets
        // In a real app, you might replace this with dynamic data
        playlist.value?.workouts?.forEach { workout ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
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

                // Text details
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(start = 16.dp)
                ) {
                    Text(text = workout.title, fontSize = 16.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(text = "# Reps: ${workout.reps ?: "-"}", fontSize = 14.sp, color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "# Sets: ${workout.sets ?: "-"}", fontSize = 14.sp, color = Color.Black)
                    }
                }
                // "..." button with dropdown menu containing Share and Remove (placeholder) // edit here
                MoreOptionsMenu(
                    onShare = {
                        Toast.makeText(context, "Share workout: ${workout.title}", Toast.LENGTH_SHORT).show()
                    },
                    onRemove = {
                        firestoreRepository.removeWorkout(
                            playlistId = playlistId,
                            workoutId = workout.id,
                            onSuccess = {
                                localFetchPlaylist()
                                Toast.makeText(context, "Removed: ${workout.title}", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Failed to remove: ${workout.title}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    ,
                    onEdit = {
                        Toast.makeText(context, "Edit clicked for workout: ${workout.title}", Toast.LENGTH_SHORT).show()
                    },

                    onTutorial = {
                        navController.navigate("tutorial")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Example bottom navigation row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            /*
            {
                Button(onClick = { navController.navigate("home") }) {
                    Text("Home")
                }
            */
            Button(onClick = { navController.navigate("library") }) {
                Text("Library")
            }
        }
    }
}
