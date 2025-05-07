package com.example.sprint0nj

import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.border

val DarkerGrey = Color(0xFF212121)

@Composable
fun TutorialScreen(navController: NavHostController, workoutId: String) {
    val workoutViewModel: WorkoutViewModel = viewModel()
    val workout by workoutViewModel.workout.collectAsState()

    LaunchedEffect(workoutId) {
        workoutViewModel.loadWorkout(workoutId)
    }

    val context = LocalContext.current
    val isLoading = workout == null
    val errorMessage = if (!isLoading && workout?.tutorialLink.isNullOrEmpty()) {
        "No tutorial available for this workout"
    } else null

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF4CAF50), Color(0x00006600))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = workout?.title ?: "Loading workout title...",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = DarkerGrey,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            ),
            modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
        )

        Text(
            text = workout?.description ?: "Loading workout description...",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = DarkerGrey,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Text(
                text = "Loading tutorial...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        workout?.tutorialLink?.let { link ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webChromeClient = WebChromeClient()
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            loadUrl(link)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121)),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF212121), shape = RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "Open in Browser or YouTube",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF212121), shape = RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "Back",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTutorialScreen() {
    val mockNavController = NavHostController(LocalContext.current)
    TutorialScreen(navController = mockNavController, workoutId = "12345")
}
