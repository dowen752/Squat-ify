package com.example.sprint0nj.data
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sprint0nj.data.Classes.Playlist
import com.example.sprint0nj.data.Classes.Workout
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val playlistsCollection = db.collection("playlists")

    // Function to add a playlist to Firestore using provided playlist
    fun postPlaylist(playlist: Playlist) {
        playlistsCollection.document(playlist.id).set(playlist)
            .addOnSuccessListener {
                Log.d("FirestoreRepo", "Playlist '${playlist.name}' successfully posted to Firestore.")
            }
            .addOnFailureListener { exception ->
                Log.d("FirestoreRepo", "Error posting playlist '${playlist.name}': ${exception.message}")
            }
    }

    // Fetches playlist by specified id. Used for displaying individual workout pages
    suspend fun fetchPlaylist(playlistId: String): Playlist? {
        return try {
            Log.d("PlaylistId", playlistId)
            val document = playlistsCollection.document(playlistId).get().await()
            if (document.exists()) {
                document.toObject(Playlist::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Maps firestore workout document to Classes.Workout object
    suspend fun fetchWorkouts(): List<Workout> {
        val snapshot = db.collection("Workouts").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Workout::class.java)
        }
    }

    
    // Removes given workout from playlist in firestore
    fun removeWorkout(
        playlistId: String,
        workoutId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val playlistRef = playlistsCollection.document(playlistId)

        playlistRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val playlist = document.toObject(Playlist::class.java)
                val updatedWorkouts = playlist?.workouts?.filter { it.id != workoutId }
                    
                playlistRef.update("workouts", updatedWorkouts)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Workout removed successfully")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Failed to update playlist", e)
                        onFailure(e)
                    }
            }
        }
    }


    // Fetching pair of all playlist ids and names, for displaying in library screen
    suspend fun fetchPlaylistSummaries(): List<Pair<String, String>> {
        val playlistNames = playlistsCollection.get().await()
        return playlistNames.documents.map { document ->
            val id = document.id
            val name = document.data?.get("name") as String ?: "Unnamed Playlist"
            id to name
        }
    }

}
