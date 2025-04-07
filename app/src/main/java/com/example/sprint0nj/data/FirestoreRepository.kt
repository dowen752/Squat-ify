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
        // Enforce unique workouts by filtering duplicates based on their ID.
        playlist.workouts = playlist.workouts.distinctBy { it.id }.toMutableList()
        Log.d("FirestoreRepo", "Posting playlist with workouts count: ${playlist.workouts.size}")

        // Update just the workouts field in the playlist document.
        playlistsCollection.document(playlist.id)
            .update("workouts", playlist.workouts)
            .addOnSuccessListener {
                Log.d("FirestoreRepo", "Playlist '${playlist.name}' successfully updated in Firestore.")
            }
            .addOnFailureListener { exception ->
                Log.d("FirestoreRepo", "Error posting playlist '${playlist.name}': ${exception.message}")
            }
    }
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
    suspend fun fetchWorkouts(): List<Workout> {
        val snapshot = db.collection("Workouts").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Workout::class.java)
        }
    }



    // Fetch all playlist IDs from firestore collection
    suspend fun fetchPlaylistIds(): List<String> {
        return try {
            val snapshot = playlistsCollection.get().await()
            snapshot.documents.mapNotNull { it.id } // Extract document IDs
        } catch (e: Exception) {
            emptyList() // Return empty list if there's an error
        }
    }

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
                if (playlist != null) {
                    val updatedWorkouts = playlist.workouts.filter { it.id != workoutId }

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
            } else {
                onFailure(Exception("Playlist not found"))
            }
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }



    suspend fun fetchPlaylistSummaries(): List<Pair<String, String>> {
        val playlistNames = playlistsCollection.get().await()
        return playlistNames.documents.map { document ->
            val id = document.id
            val name = document.data?.get("name") as String ?: "Unnamed Playlist"
            id to name
        }
    }

    fun getPlaylistsFlow(): Flow<List<Pair<String, String>>> = callbackFlow {
        val listenerRegistration = db.collection("playlists")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error ?: Exception("Snapshot is null"))
                    return@addSnapshotListener
                }

                val summaries = snapshot.documents.mapNotNull {
                    val id = it.id
                    val name = it.getString("name")
                    if (name != null) id to name else null
                }

                trySend(summaries)
            }

        awaitClose { listenerRegistration.remove() }
    }


}
