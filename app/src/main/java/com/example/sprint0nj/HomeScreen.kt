package com.example.sprint0nj//THIS CODE WILL BE SAVED FOR SPRINT 2
/*

package com.example.sprint0nj

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    // Example layout that might look like your image
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Green background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top search bar
        Text(
            text = "SEARCH",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Workouts area (just some placeholder squares)
        Text(
            text = "RECENT WORKOUTS",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Thing 1 / Thing 2" row or other content
        Text(
            text = "Thing 1 / Thing 2",
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { navController.navigate("home") }) {
                Text(text = "Home")
            }
            Button(onClick = { navController.navigate("library") }) {
                Text(text = "Library")
            }
        }
    }
}

*/