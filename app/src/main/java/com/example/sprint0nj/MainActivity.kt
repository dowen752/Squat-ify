package com.example.sprint0nj

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestoreRepository = remember { FirestoreRepository() }
    val playlists = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var selectedUserId = FirebaseAuth.getInstance().currentUser?.uid
    var sharePlaylistID = ""




    val localRefreshPlaylists = {
        if (selectedUserId != null) {
            scope.launch {
                val updated = firestoreRepository.fetchPlaylistSummaries(
                    selectedUserId,
                    onResult = {updated->
                        playlists.value = updated
                    }
                )
            }
        }
    }

// State variable to control the display of the ShareDialog
    var showShareDialog by remember { mutableStateOf(false) }

    // State variable to hold the playlist information that will be shared
    var selectedPlaylistForShare by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Controls the display of the rename dialog
    var showRenameDialog by remember { mutableStateOf(false) }
// Holds the playlist to rename, using a Pair of ID and name
    var playlistToRename by remember { mutableStateOf<Pair<String, String>?>(null) }


//    val localFetchPlaylists = {
//        scope.launch{
//            val updated = firestoreRepository.fetchPlaylist(playlistId)
//            playlist.value = updated
//        }
//    }

    LaunchedEffect(Unit) {
        firestoreRepository.fetchPlaylistSummaries(userId = selectedUserId!!){ summaries ->
            playlists.value = summaries
        }

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
                ),
                onPlaylistAdded = {
                    localRefreshPlaylists()
                }
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
                        .background(Color(0xFF212121), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Clicking the text navigates to the WorkoutScreen
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        color = Color.White,
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
                            // When share is clicked, save the playlist info and show the ShareDialog.
                            // "id to name" is shorthand for Pair(id, name)
                            sharePlaylistID = id
                            selectedPlaylistForShare = id to name
                            sharePlaylistID = id
                            showShareDialog = true
                        },

                        // Previous code:
                       /* onShare = { // destUsername will be replaced with user input once we have pop up
                            firestoreRepository.sharePlaylist(destUsername = "Thats Gonna Leave A Marc",
                                playlistId = id,
                                onSuccess = {
                                    Toast.makeText(context, "Shared playlist: $name", Toast.LENGTH_SHORT).show()
                                }
                                )
                        },*/
                        onRemove = {
                            firestoreRepository.removePlaylist(
                                userId = selectedUserId!!,
                                playlistId = id,
                                onSuccess = {
                                    localRefreshPlaylists()
                                    Toast.makeText(context, "Remove clicked for playlist: $name", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onEdit = {
                            // When "Edit" is clicked, store this playlist's data
                            // "id to name" is shorthand for Pair(id, name)
                            playlistToRename = id to name
                            showRenameDialog = true
                        }
                    )
                }
            }
        }
    }

    // This is for "Share"

    if (showShareDialog && selectedPlaylistForShare != null) {
    ShareDialog(
        onDismiss = {
            // When the dialog is dismissed, reset the share-related state
            showShareDialog = false
            selectedPlaylistForShare = null
        },
        onConfirm = { friendUsername ->
            // [Firebase Placeholder]


            val playlistID = sharePlaylistID

            firestoreRepository.sharePlaylist(destUsername = friendUsername,
                playlistId = playlistID,
                onSuccess = {
                    Toast.makeText(
                        context,
                        "Shared playlist (${selectedPlaylistForShare!!.second}) with $friendUsername",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            // For now, just showing Toast message (can delete or keep)

            // Reset state after confirming
            showShareDialog = false
            selectedPlaylistForShare = null
        }
    )
}

    // This dialog is shown when "Edit" is selected on a playlist

    if (showRenameDialog && playlistToRename != null) {
        RenamePlaylistDialog(
            currentName = playlistToRename!!.second, // Pre-populates with the existing playlist name
            onDismiss = {
                // Reset rename state when dismissed
                showRenameDialog = false
                playlistToRename = null
            },
            onConfirm = { newName ->
                // [Firebase Placeholder]
                // For now, just showing Toast message (can delete or keep)
                Toast.makeText(
                    context,
                    "Playlist renamed to: $newName",
                    Toast.LENGTH_SHORT
                ).show()
                // Reset state and refresh playlists
                showRenameDialog = false
                playlistToRename = null
                localRefreshPlaylists()
            }
        )
    }
}