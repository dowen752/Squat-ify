package com.example.sprint0nj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.platform.LocalContext
import com.example.sprint0nj.data.FirestoreRepository
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.sprint0nj.MoreOptionsMenu

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
    val firestoreRepository = remember { FirestoreRepository() }
    val playlists = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    LaunchedEffect(Unit) {
        playlists.value = firestoreRepository.fetchPlaylistSummaries()
    }

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
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        // LAZY COLUMN is our whole scrolling feature, this allows us to scroll when the list gets too big for the screen
        LazyColumn {
            items(playlists.value) { (id, name) ->
                // We replaced the single Button with a Row that includes clickable text and a "..." dropdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Clicking the text navigates to the WorkoutScreen
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                // Navigate to the WorkoutScreen route
                                navController.navigate("workout/$id")
                            }
                    )
                    // "..." button with dropdown menu containing Share and Remove (placeholder)
                    MoreOptionsMenu(
                        onShare = {
                            Toast.makeText(context, "Share playlist: $name", Toast.LENGTH_SHORT).show()
                        },
                        onRemove = {
                            // Placeholder: no real removal logic
                            Toast.makeText(context, "Remove clicked for playlist: $name", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = {
                            // Placeholder: no real removal logic
                            Toast.makeText(context, "Edit clicked: $name", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
