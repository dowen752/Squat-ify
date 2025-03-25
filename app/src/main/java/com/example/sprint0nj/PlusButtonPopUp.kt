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




// Data class representing a single menu option
// This class holds the title (what is displayed in the menu) and an action (lambda) to execute on click
data class MenuOption(
    val title: String,       // The text displayed for the menu option
    val onClick: () -> Unit  // The action executed when the option is selected
)

@Composable
fun PlaylistNameDialog(
    onDismiss: () -> Unit,      // Called to dismiss the dialog
    onConfirm: (String) -> Unit // Called with the entered playlist name when confirmed
) {
    var playlistName by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Playlist Name:") },
        text = {
            // BasicTextField to allow the user to type in the playlist name
            BasicTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        },
        confirmButton = {
            // Confirm button returns the entered playlist name
            Button(
                onClick = {
                    /*

                    Replace Toast with firestore integration for playlists


                    */
                    onConfirm(playlistName.text) // Pass the input to the onConfirm callback
                    onDismiss() // Close the dialog after confirming
                },

                 colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                // Can also modify the shape, padding, etc.
            ) {
                // Customize the text style here (font size, color, etc.)
                Text("Confirm")
            }
        },
        dismissButton = {
            // Cancel Button: Customize its UI similarly to the Confirm Button
            Button(
                onClick = { onDismiss() },
                // For changing the Cancel button's appearance
                 colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                // Can add any padding or shape modifications here.
            ) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun WorkoutNameDialog(
    onDismiss: () -> Unit,      // Called to dismiss the dialog
    onConfirm: (String) -> Unit // Called with the entered workout name when confirmed
) {
    var workoutName by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Workout Name:") }, // Title changed for workout
        text = {
            // BasicTextField to allow the user to type in the workout name
            BasicTextField(
                value = workoutName,
                onValueChange = { workoutName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        },
        confirmButton = {
            // Confirm button returns the entered workout name
            Button(
                onClick = {
                    /*

                    Replace Toast with firestore integration for workouts.


                    */

                    onConfirm(workoutName.text) // Pass the input to the onConfirm callback
                    onDismiss() // Close the dialog after confirming
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            // Cancel Button for workout
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Cancel")
            }
        }
    )
}


// Reusable composable for the plus button with a popup (dropdown) menu
// This component can be used in multiple screens by passing different lists of MenuOption items
@Composable
fun PlusButtonWithMenu(
    menuOptions: List<MenuOption>  // A list of menu options to display in the dropdown
) {
    // Local state to track whether the dropdown menu is currently expanded
    var menuExpanded by remember { mutableStateOf(false) }
    // State to control the visibility of the Playlist dialog
    var showPlaylistDialog by remember { mutableStateOf(false) }
    // State to control the visibility of the Workout dialog
    var showWorkoutDialog by remember { mutableStateOf(false) }

    // Capture the context once in this composable scope
    val context = LocalContext.current

    // Box is used as a container to anchor both the plus button and its dropdown menu
    // The wrapContentSize with Alignment.TopEnd places content at the top-right corner
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {


       // Plus Button:
        // This button displays a "+" symbol and triggers the dropdown menu when clicked
        Button(
            onClick = { menuExpanded = true }, // When clicked, set menuExpanded to true to open the menu
            modifier = Modifier.size(56.dp),     // Set the fixed size of the button (can be adjusted).
            shape = RoundedCornerShape(12.dp),   // Rounded corners. Can change the dp value to alter curvature
            colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Button background color
            contentPadding = PaddingValues(0.dp)  // Remove any internal padding for a tighter layout
        ) {

            // Inner Box to center the "+" text inside the button
            Box(
                modifier = Modifier.fillMaxSize(),        // Fill the available space inside the button
                contentAlignment = Alignment.Center         // Center the text both vertically and horizontally
            ) {
                // Text displaying the plus sign
                // Can adjust fontSize and fontWeight for customization
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }


        // DropdownMenu:
        // This menu appears when menuExpanded is true and displays the list of menu options
        DropdownMenu(
            expanded = menuExpanded,                   // Controls whether the menu is visible
            onDismissRequest = { menuExpanded = false }, // Callback to close the menu when clicked outside
            // The offset positions the dropdown menu relative to the plus button
            // Change the DpOffset values to adjust horizontal (x) or vertical (y) position
            offset = DpOffset(0.dp, 0.dp)
        ) {
            // Loop through each menu option provided in the list
            menuOptions.forEach { option ->
                // Each option is displayed as a DropdownMenuItem
                DropdownMenuItem(
                    text = { Text(option.title) },
                    onClick = {
                        when (option.title) {
                            "Add Playlist" -> {
                                showPlaylistDialog = true // Show the playlist dialog
                            }
                            "Add Workout" -> {
                                showWorkoutDialog = true // Show the workout dialog
                            }
                            else -> {
                                option.onClick() // For other options, run their defined action
                            }
                        }
                        menuExpanded = false // Close the dropdown menu after selection
                    }
                )
            }
        }

        // Display the PlaylistNameDialog when showPlaylistDialog is true
        if (showPlaylistDialog) {
            PlaylistNameDialog(
                onDismiss = { showPlaylistDialog = false },
                onConfirm = { playlistName ->
                    /*

                    Replace Toast with firestore integration to add the new playlist to database.


                    */
                    Toast.makeText(context, "Playlist added: $playlistName", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Display the WorkoutNameDialog when showWorkoutDialog is true
        if (showWorkoutDialog) {
            WorkoutNameDialog(
                onDismiss = { showWorkoutDialog = false },
                onConfirm = { workoutName ->
                    /*

                    Replace Toast with firestore integration to add the new workout to database.


                    */
                    Toast.makeText(context, "Workout added: $workoutName", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
