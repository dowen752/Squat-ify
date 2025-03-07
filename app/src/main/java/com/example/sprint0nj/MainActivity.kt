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
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            LibraryScreen()
        }
    }
}

@Composable
fun LibraryScreen() {
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

        // Plus Button (Now Fully Centered)
        Button(
            onClick = { /* Handle adding a new playlist */ },
            modifier = Modifier
                .align(Alignment.End)
                .size(56.dp), // Keep size for better visibility
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(0.dp) // Removes extra padding inside the button
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), // Ensures text is centered
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val playlists = listOf("Playlist 1", "Playlist 2", "Playlist 3", "Playlist 4", "Playlist 5")

        playlists.forEach { playlistName ->
            Button(
                onClick = { /* Handle button click */ },
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
