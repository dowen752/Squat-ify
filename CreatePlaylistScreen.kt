import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CreatePlaylistScreen(navController: NavController) {
    var playlistName by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Create New Playlist", style = MaterialTheme.typography.headlineSmall)

        TextField(
            value = playlistName,
            onValueChange = { playlistName = it },
            label = { Text("Playlist Name") }
        )

        Button(
            onClick = {
                if (playlistName.isNotEmpty()) {
                    savePlaylistToFirebase(playlistName)
                    navController.popBackStack() // Return to the PlaylistsScreen
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save Playlist")
        }
    }
}

fun savePlaylistToFirebase(name: String) {
    val db = FirebaseFirestore.getInstance()
    val playlist = hashMapOf("name" to name)

    db.collection("workout_playlists")
        .add(playlist)
        .addOnSuccessListener { println("Playlist added!") }
        .addOnFailureListener { e -> println("Error: $e") }
}
