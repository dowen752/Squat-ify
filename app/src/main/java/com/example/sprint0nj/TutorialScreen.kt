package com.example.sprint0nj

import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel

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

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = workout?.title ?: "Loading workout title...",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = workout?.description ?: "Loading workout description...",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Text(text = "Loading tutorial...")
        }

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        workout?.tutorialLink?.let { link ->
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(link)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Open in Browser or YouTube")
            }
        }
    }
}
