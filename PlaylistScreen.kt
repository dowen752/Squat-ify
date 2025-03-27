import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PlaylistsScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_playlist") }) {
                Text("+")
            }
        }
    ) {
        Text(text = "Your Workout Playlists", modifier = Modifier.padding(16.dp))
        // Optionally, add a LazyColumn to display playlists.
    }
}