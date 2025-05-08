package com.example.sprint0nj

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import com.example.sprint0nj.data.FirestoreRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

// PlusButtonWithMenu and MenuOption
import com.example.sprint0nj.PlusButtonWithMenu
import com.example.sprint0nj.MenuOption

@Composable
fun FriendsScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val displayName = currentUser?.displayName ?: currentUser?.email ?: "Unknown User"
    val context = LocalContext.current
    val firestoreRepository = remember { FirestoreRepository() }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val friends = remember { mutableStateOf<List<String>>(emptyList()) }

    // Replace with real Firestore data later
    LaunchedEffect(currentUserId) {
        currentUserId?.let {
            firestoreRepository.fetchFriendsList(it) { fetched ->
                friends.value = fetched
            }
        }
    }

    // New state for showing the “add friend” dialog and capturing the entry
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var newFriendName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.friendslistbg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Title + plus-button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "My Friends",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // “+” button added here
                PlusButtonWithMenu(
                    menuOptions = listOf(
                        MenuOption("Add Friend") {
                            showAddFriendDialog = true
                        }
                    ),
                    onPlaylistAdded = { /* no‐op or refresh list */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn {
                items(friends.value) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(96.dp)
                            .background(Color(0xFF212121), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = friend,
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (currentUserId != null) {
                                    firestoreRepository.removeFriend(currentUserId, friend) {
                                        firestoreRepository.fetchFriendsList(currentUserId) { updatedList ->
                                            friends.value = updatedList
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text("Remove", color = Color.White)
                        }
                    }
                }
            }


        // Logout Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    // navigate back to login screen
                    navController.navigate("login") {
                        // clear backstack so they can't press Back to return here
                        popUpTo("login") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))

            ) {
                Text("Log out", color = Color.White, fontWeight = FontWeight.Bold)

            }
        }
    }

        // “Add Friend” pop-up
        if (showAddFriendDialog) {
            AlertDialog(
                onDismissRequest = { showAddFriendDialog = false },
                title = { Text("Add a Friend") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newFriendName,
                            onValueChange = { newFriendName = it },
                            label = { Text("Enter a Username") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {

                        if (currentUserId != null) {
                            val userRef = FirebaseFirestore.getInstance().collection("users").document(currentUserId)
                            userRef.update("userFriends", FieldValue.arrayUnion(newFriendName))
                                .addOnSuccessListener {
                                    firestoreRepository.fetchFriendsList(currentUserId) { updatedList ->
                                        friends.value = updatedList
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to add friend", Toast.LENGTH_SHORT).show()
                                }
                        }

                        newFriendName = ""
                        showAddFriendDialog = false
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                        Text("Confirm", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(onClick = { showAddFriendDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

