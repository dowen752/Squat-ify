package com.example.sprint0nj

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Main green background
    ) {
        // Main Column for title and buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Title
            Text(
                text = "Squat-ify",
                fontSize = 32.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username button (placeholder) in dark grey
            Button(
                onClick = { /* Placeholder */ },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
            ) {
                Text(text = "Username", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password button (placeholder) in dark grey
            Button(
                onClick = { /* Placeholder */ },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
            ) {
                Text(text = "Password", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Log In button in dark grey, navigates to the Library screen
            Button(
                onClick = { navController.navigate("library") },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
            ) {
                Text(text = "Log In", color = Color.White)
            }
        }

        // Bypass square in the bottom-left with a slightly different shade of green
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF388E3C)) // Slightly different green shade
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .clickable { navController.navigate("library") }
        )
    }
}
