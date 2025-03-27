package com.example.sprint0nj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.widget.Toast // "Toast" is an Android API used to display the short confirmation messages after clicking the buttons
import androidx.compose.ui.platform.LocalContext
import com.example.sprint0nj.data.FirestoreRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hardcoding playlists, will remove soon

//        val firestoreRepository = FirestoreRepository()
//        val myPlaylist = Playlist(id = "0003",
//            name = "Leg Day",
//            workouts = mutableListOf(
//                WorkoutMods.addWorkout(1, "Squats", null, 8, 3, "Standard squats, focus on depth."),
//                WorkoutMods.addWorkout(2, "Leg Extensions", null, 12, 3, "Moderate weight leg extensions."),
//                WorkoutMods.addWorkout(3, "Machine Leg Curls", null, 12, 3, "Standard leg curls."),
//                WorkoutMods.addWorkout(4, "Deadlifts", null, 12, 3, "Deadlifts with light to moderate weight.")
//            )
//        )
//        firestoreRepository.postPlaylist(myPlaylist)


        setContent {
            AppNavHost()  // Show your NavHost here
        }
    }
}

@Composable
fun LibraryScreen(navController: NavHostController) {
    val context = LocalContext.current
    // Object for firestore methods
    val firestoreRepository = remember {FirestoreRepository()}
    // List of playlist ids and names
    val playlists = remember { mutableStateListOf<Pair<String, String>>()}
    // Allows for asynchronous execution
    val coroutineScope = rememberCoroutineScope()
    // Callback function passed to PlusButtonPopUp to allow page refreshing on playlist creation
    val onPlaylistAdded: () -> Unit = {
        coroutineScope.launch {
            val updated = firestoreRepository.fetchPlaylistSummaries()
            playlists.clear()
            playlists.addAll(updated)
        }
    }
    // Callback for page refreshing on workout creation
//    val onWorkoutAdded: () -> Unit = {
//        coroutineScope.launch {
//            val updatedWorkouts = firestoreRepository.fetchWorkouts() // you'd need to implement this
//            workouts.clear()
//            workouts.addAll(updatedWorkouts)
//        }
//    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Green background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "My Playlists",
            fontSize = 28.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        //TAKES YOU TO HOME SCREEN
        /*
        Button(onClick = { navController.navigate("home") }) {
            Text("Go to Home")
        }
        */

        // A Box is used as a container that fills the available width
        // The contentAlignment parameter ensures that the children (the plus button) is positioned at the top-right of the Box
        // "Toast" is an Android API used to display the short confirmation messages after clicking the buttons
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            // Call the reusable PlusButtonWithMenu composable
            // List of MenuOption objects is passed to define the menu items
            PlusButtonWithMenu(
                menuOptions = listOf(
                    // First menu option with the title "Add Playlist"
                    // When clicked, a Toast message is displayed
                    MenuOption("Add Playlist") {
                        //Toast.makeText(context, "Add Playlist clicked", Toast.LENGTH_SHORT).show()
                    },
                    // Second menu option with the title "Import Playlist"
                    // When clicked, a Toast message is displayed
                    MenuOption("Import Playlist") {
                        Toast.makeText(context, "Import Playlist clicked", Toast.LENGTH_SHORT).show()
                    }
                ),
                onPlaylistAdded = onPlaylistAdded

            )

        }


        Spacer(modifier = Modifier.height(16.dp))

        playlists.forEach { (id, name) ->
            Button(      // Navigate to the WorkoutScreen route
                onClick = {
                    navController.navigate("workout/$id")
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = name, fontSize = 16.sp, color = Color.Black)
            }
        }
    }
}
