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
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.platform.LocalContext
import com.example.sprint0nj.data.FirestoreRepository
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.sprint0nj.MoreOptionsMenu
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.sprint0nj.SearchPlaylistsButton

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
    val currentUser = FirebaseAuth.getInstance().currentUser
    val displayName = currentUser?.displayName ?: currentUser?.email ?: "Unknown User"
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var displayedPlaylists by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var pendingQuery by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf<String?>(null) }


    val localRefreshPlaylists = {
        if (selectedUserId != null) {
            scope.launch {
                val updated = firestoreRepository.fetchPlaylistSummaries(
                    selectedUserId,
                    onResult = { updated ->
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
        firestoreRepository.fetchPlaylistSummaries(userId = selectedUserId!!) { summaries ->
            playlists.value = summaries
            displayedPlaylists = summaries
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.squatmainbg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                // changes user icon and username position
                .padding(start = 4.dp, end = 16.dp, top = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 12.dp)
                    .clickable {
                        navController.navigate("friends")
                    }
            )
            Text(
                text = displayName,
                color = Color.White,
                fontSize = 20.sp, // increased from 16.sp to 20.sp
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(100.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Playlists",
                    fontSize = 28.sp,
                    color = Color.White
                )
                // for grouping the two icons together
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchPlaylistsButton(onClick = { showSearchDialog = true })
                    PlusButtonWithMenu(
                        menuOptions = listOf(
                            MenuOption("Add Playlist") { showAddDialog = true }
                        ),
                        onPlaylistAdded = { localRefreshPlaylists() }
                    )
                }
            }

            // Space between "My Playlists" and list of actual playlists

            //Spacer(modifier = Modifier.height(2.dp))

            // LAZY COLUMN is our whole scrolling feature, this allows us to scroll when the list gets too big for the screen
            LazyColumn {
                items(displayedPlaylists) { (id, name) ->
                    // We replaced the single Button with a Row that includes clickable text and a "..." dropdown
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(96.dp)
                            .background(Color(0xFF212121), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
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
                            firestoreRepository.sharePlaylist(destUsername = "That's Gonna Leave A Marc",
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
                                        Toast.makeText(
                                            context,
                                            "Remove clicked for playlist: $name",
                                            Toast.LENGTH_SHORT
                                        ).show()
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

        if (showAddDialog) {
            PlaylistNameDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { newName ->
                    // [Firebase placeholder] firestoreRepository.postPlaylist(...)
                    Toast.makeText(context, "Playlist added: $newName", Toast.LENGTH_SHORT).show()
                    localRefreshPlaylists()
                    showAddDialog = false
                },
                onPlaylistAdded = { /*not used here*/ }
            )
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


                    val playlistID = selectedPlaylistForShare?.first ?: "Not Working"
                    val playlistName = selectedPlaylistForShare?.second ?: "Unknown Playlist"
                    firestoreRepository.sharePlaylist(
                        destUsername = friendUsername,
                        playlistId = playlistID,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Shared $playlistName with $friendUsername",
                                Toast.LENGTH_SHORT
                            ).show()
                            showShareDialog = false
                            selectedPlaylistForShare = null
                        },
                        onFailure = {
                            Toast.makeText(
                                context,
                                "User not found.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    // For now, just showing Toast message (can delete or keep)

                    // Reset state after confirming

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

        if (showSearchDialog) {
            SearchPlaylistsDialog(
                onDismiss = {
                    showSearchDialog = false
                },
                onConfirm = { query ->
                    searchQuery = query
                    showSearchDialog = false
                }
            )
        }

        LaunchedEffect(searchQuery) {
            if (searchQuery.isNullOrBlank()) {
                displayedPlaylists = playlists.value
            } else {
                displayedPlaylists = playlists.value.filter {
                    it.second.contains(searchQuery ?: "", ignoreCase = true)
                }
            }
        }

    }
}