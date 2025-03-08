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

@Composable
fun WorkoutScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Green background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row with "Playlist #1" and a plus button on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Playlist #1",
                fontSize = 24.sp,
                color = Color.White
            )

            Button(
                onClick = { /* Add new workout or item */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.size(40.dp)
            ) {
                Text("+", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Example list of workouts with #Reps and #Sets
        // In a real app, you might replace this with dynamic data
        val workouts = listOf(
            "Workout A",
            "Workout B",
            "Workout C",
            "Workout D"
        )

        workouts.forEach { workout ->
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
                    Text(text = workout, fontSize = 16.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(text = "# Reps:", fontSize = 14.sp, color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "# Sets:", fontSize = 14.sp, color = Color.Black)
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
