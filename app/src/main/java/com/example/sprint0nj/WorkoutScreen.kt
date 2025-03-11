package com.example.sprint0nj

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.sprint0nj.data.FirestoreRepository
import com.example.sprint0nj.data.Classes.Playlist

@Composable
fun WorkoutScreen(navController: NavController) {
    // This captures the current context which is used in the callbacks for popup
    val context = LocalContext.current
    val firestoreRepository = remember { FirestoreRepository()}
    val scope = rememberCoroutineScope()
    val playlist = remember { mutableStateOf<Playlist?>(null) }
    LaunchedEffect(Unit) {
        playlist.value = firestoreRepository.fetchPlaylist("0000")
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

            // Call the reusable plus button
            PlusButtonWithMenu(
                menuOptions = listOf(
                    // First menu option with the title "Add Workout"
                    // When clicked, a Toast message is displayed
                    MenuOption("Add Workout") {
                        Toast.makeText(context, "Add Workout clicked", Toast.LENGTH_SHORT).show()
                    },
                    // Second menu option with the title "Import Workout" (probably could use a different title)
                    // When clicked, a Toast message is displayed
                    MenuOption("Import Workout") {
                        Toast.makeText(context, "Import Workout clicked", Toast.LENGTH_SHORT).show()
                    }
                )
            )
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
                    modifier = Modifier.weight(1f, fill = false).padding(start = 16.dp)
                ) {
                    Text(text = workout.title, fontSize = 16.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(text = "# Reps: ${workout.reps ?: "-"}", fontSize = 14.sp, color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "# Sets: ${workout.sets ?: "-"}", fontSize = 14.sp, color = Color.Black)
                    }
                }
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

@Preview(showBackground = true)
@Composable
fun WorkoutScreenPreview() {
    val navController = rememberNavController() // Only works if you have Navigation Compose
    WorkoutScreen(navController = navController)
}