package com.example.sprint0nj

// Import the separate MoreOptionsMenu composable from its own file.
import android.R.attr.fontWeight
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sprint0nj.data.Classes.Playlist
import com.example.sprint0nj.data.Classes.Workout
import com.example.sprint0nj.data.FirestoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sprint0nj.WorkoutTimerViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource


@Composable
fun WorkoutScreen(navController: NavController, playlistId: String) {
    // This captures the current context which is used in the callbacks for popup
    val context = LocalContext.current
    val firestoreRepository = remember { FirestoreRepository() }
    val scope = rememberCoroutineScope()
    val playlist = remember { mutableStateOf<Playlist?>(null) }
    val checkedWorkouts = remember { mutableStateMapOf<String, Boolean>() }
    // This block will pause the timer when the screen is disposed or goes to background
    val timerViewModel: WorkoutTimerViewModel = viewModel()
    val timerSeconds by timerViewModel.timerSeconds.collectAsState()
    val isTimerRunning by timerViewModel.isTimerRunning.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current


    // This block will pause the timer when the screen is disposed or goes to background
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP,
                Lifecycle.Event.ON_DESTROY -> timerViewModel.pauseTimer()

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            timerViewModel.pauseTimer()
        }
    }

    // State to hold the workout entry for editing; if null, we're in add mode
    var workoutToEdit by remember { mutableStateOf<WorkoutEntry?>(null) }

    val localFetchPlaylist = {
        scope.launch {
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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.squatmainbg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //TITLE FOR THE PLAYLISTS
            Text(
                text = playlist.value?.name ?: "Unnamed Playlist",
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp)

            )

            // Shifted this down
            Spacer(modifier = Modifier.height(60.dp))

            if (playlist.value == null) { // Loading if playlist hasn't been fetched yet
                Text(text = "Loading...", fontSize = 20.sp, color = Color.Black)
                return@Column
            }
            // Top row with "Playlist #1" and a plus button on the right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        if (isTimerRunning) timerViewModel.pauseTimer()
                        else timerViewModel.startTimer()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .width(66.dp)
                        .height(56.dp)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = if (isTimerRunning) "■" else "▶",
                        fontSize = 36.sp,
                        color = Color.White
                    )
                }

                // Display the timer value:
                Text(
                    text = "Time: ${timerSeconds / 60}:${
                        (timerSeconds % 60).toString().padStart(2, '0')
                    }",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 12.dp)

                )

                //button for adding workouts
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
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
                                Toast.makeText(
                                    context,
                                    "Import Workout clicked",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                items(playlist.value!!.workouts) { workout ->
                    val context = LocalContext.current

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(96.dp)
                            .background(Color(0xFF212121), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left placeholder icon/box
                        //CHECKBOX TO MARK WORKOUTS DONE
                        Box(
                            modifier = Modifier.scale(1.5f) // Scale up the checkbox
                        ) {
                            Checkbox(
                                checked = checkedWorkouts[workout.id] == true,
                                onCheckedChange = { isChecked ->
                                    checkedWorkouts[workout.id] = isChecked
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF4CAF50), // Green fill when checked
                                    uncheckedColor = Color.White,     // White border when unchecked
                                    checkmarkColor = Color.Black      // Color of the checkmark itself
                                )
                            )
                        }

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
                            onShare = null,
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
                                navController.navigate("tutorial/${workout.id}")
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
                    Text("Home", color = Color.White)
                }
            }
        }
    }
}
