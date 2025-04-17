package com.example.sprint0nj

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast

@Composable
fun ShareDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit  // This callback receives the friend's username to share with
) {
    val context = LocalContext.current

    // State for manual entry of a friend's username
    var manualFriendName by remember { mutableStateOf(TextFieldValue("")) }

    // State for controlling the dropdown of friend names
    var isFriendDropdownExpanded by remember { mutableStateOf(false) }

    // This state holds the friend selected from the dropdown independent of manualFriendName.

    var dropdownSelectedFriend by remember { mutableStateOf("") }

    // Sample list of friend names
    // Can delete and fetch from Firebase instead
    val friendList = remember { mutableStateOf(listOf("Andrew", "Nick", "Davis", "Marc")) }
    Log.d("ShareDialog", "Share Dialog entering correctly")
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Share Playlist") },
        text = {
            Column {
                // Manual entry section
                Text(text = "Enter a username:", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    value = manualFriendName,
                    onValueChange = { manualFriendName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown section for selecting a friend
                Text(text = "Or select from your friends:", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { isFriendDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
                    ) {
                        // Display the chosen friend if available
                        Text(
                            text = if (dropdownSelectedFriend.isEmpty()) "Select Friend" else dropdownSelectedFriend,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = isFriendDropdownExpanded,
                        onDismissRequest = { isFriendDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        friendList.value.forEach { friend ->
                            DropdownMenuItem(
                                text = { Text(friend) },
                                onClick = {
                                    // When a friend is selected, update the selection
                                    dropdownSelectedFriend = friend
                                    // Clear the manual text field to indicate that the dropdown selection is used
                                    manualFriendName = TextFieldValue("")
                                    isFriendDropdownExpanded = false
                                }
                            )
                        }
                        // [Firebase Placeholder] Replace the hardcoded friendList with dynamic data here?
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Log.d("ShareDialog", "Share Dialog reachingOnClick")
                    // Determine which friend username to use:
                    // If the manual text field is non-empty, use this
                    // Otherwise, use the friend selected from the dropdown
                    val friendUsername = if (manualFriendName.text.isNotEmpty()) {
                        manualFriendName.text
                    } else {
                        dropdownSelectedFriend
                    }

                    if (friendUsername.isNotEmpty()) {
                        // [Firebase Placeholder] Insert Firebase share logic?
                        onConfirm(friendUsername)
                        onDismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Please enter or select a friend's username.",
                            Toast.LENGTH_SHORT
                        ).show()
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
