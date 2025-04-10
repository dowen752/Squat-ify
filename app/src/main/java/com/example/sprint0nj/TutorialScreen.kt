package com.example.sprint0nj

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun TutorialScreen(navController: NavHostController) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Tutorial for Workouts", style = MaterialTheme.typography.bodyLarge) // Updated to bodyLarge

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "This tutorial guide will help you through the proper form and technique for the workout you want to do!")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/tutorial"))
            context.startActivity(intent)
        }) {
            Text(text = "Open Tutorials")
        }
    }
}

@Composable
fun PreviewTutorialScreen() {
    TutorialScreen(navController = rememberNavController())
}
