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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.widget.Toast // "Toast" is an Android API used to display the short confirmation messages after clicking the buttons
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavHost()  // Show your NavHost here
        }
    }
}

@Composable
fun LibraryScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Green background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "My Lists",
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

        // Box container to hold the plus button and its dropdown menu
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd // Align the content to the top-right corner
        ) {
            // State variable to control whether the dropdown menu is shown
            var menuExpanded by remember { mutableStateOf(false) }
            // Get the current context to display Toast messages
            val context = LocalContext.current

            // The plus button that triggers the dropdown menu
            Button(
                onClick = { menuExpanded = true }, // When clicked, open the dropdown menu
                modifier = Modifier
                    .size(56.dp), // Fixed size for the button
                shape = RoundedCornerShape(12.dp), // Rounded corners
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(0.dp) // No extra internal padding
            ) {
                // Center the "+" text inside the button
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // DropdownMenu that appears when the plus button is clicked
            // DPOffset moves the menu horizontally and vertically
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                offset = DpOffset(x = (275).dp, y = (5).dp)
            ) {
                // "Add Playlist" menu item
                DropdownMenuItem(
                    text = { Text("Add Playlist") },
                    onClick = {
                        Toast.makeText(context, "Add Playlist clicked", Toast.LENGTH_SHORT).show()
                        menuExpanded = false // Close the menu after selection
                    }
                )
                // "Import Playlist" menu item.
                DropdownMenuItem(
                    text = { Text("Import Playlist") },
                    onClick = {
                        Toast.makeText(context, "Import Playlist clicked", Toast.LENGTH_SHORT).show()
                        menuExpanded = false // Close the menu after selection
                    }
                )
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        val playlists = listOf("Playlist 1", "Playlist 2", "Playlist 3", "Playlist 4", "Playlist 5")

        playlists.forEach { playlistName ->
            Button(      // Navigate to the WorkoutScreen route
                onClick = {
                    navController.navigate("workout")
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = playlistName, fontSize = 16.sp, color = Color.Black)
            }
        }
    }
}
