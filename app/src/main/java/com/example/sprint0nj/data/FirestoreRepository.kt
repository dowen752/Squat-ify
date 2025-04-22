package com.example.sprint0nj.data

import android.util.Log
import com.example.sprint0nj.data.Classes.Playlist
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.sprint0nj.data.Classes.Workout

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val playlistsCollection = db.collection("playlists")
    private val workoutsCollection = db.collection("Workouts")

    // Fetching pair of all playlist IDs and names for the Library screen
    fun fetchPlaylistSummaries(userId: String, onResult: (List<Pair<String, String>>) -> Unit) {
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val playlistIds = document.get("playlistIds") as? List<String> ?: emptyList()
                if (playlistIds.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                playlistsCollection
                    .whereIn(FieldPath.documentId(), playlistIds)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val summaries = querySnapshot.documents.mapNotNull { doc ->
                            doc.getString("name")?.let { name -> doc.id to name }
                        }
                        onResult(summaries)
                    }
            } else {
                onResult(emptyList())
            }
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to fetch playlist summaries: ${it.message}")
            onResult(emptyList())
        }
    }

    // Add a new playlist and update the user's playlist list
    fun postPlaylist(playlist: Playlist, userId: String, onSuccess: () -> Unit = {}) {
        val playlistRef = playlistsCollection.document(playlist.id)
        val userRef = db.collection("users").document(userId)

        db.runBatch { batch ->
            batch.set(playlistRef, playlist)
            batch.update(userRef, "playlistIds", FieldValue.arrayUnion(playlist.id))
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to post playlist: ${it.message}")
        }
    }

    // Fetch a single playlist by its ID
    suspend fun fetchPlaylist(playlistId: String): Playlist? {
        return try {
            val document = playlistsCollection.document(playlistId).get().await()
            document.toObject(Playlist::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching playlist $playlistId: ${e.message}")
            null
        }
    }

    // Fetch all workouts from Firestore
    suspend fun fetchWorkouts(): List<Workout> {
        return try {
            val snapshot = workoutsCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Workout::class.java) }
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching workouts: ${e.message}")
            emptyList()
        }
    }

    // Fetch a single workout by its ID — used for tutorial screen
    suspend fun fetchWorkoutById(workoutId: String): Workout? {
        return try {
            val doc = db.collection("Workouts").document(workoutId).get().await()
            if (doc.exists()) {
                doc.toObject(Workout::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching workout: ${e.message}")
            null
        }
    }

    // Remove a workout from a specific playlist
    fun removeWorkout(playlistId: String, workoutId: String, onSuccess: () -> Unit = {}) {
        val playlistRef = playlistsCollection.document(playlistId)

        playlistRef.get().addOnSuccessListener { document ->
            val playlist = document.toObject(Playlist::class.java)
            val updatedWorkouts = playlist?.workouts?.filter { it.id != workoutId }

            playlistRef.update("workouts", updatedWorkouts).addOnSuccessListener {
                Log.d("Firestore", "Workout removed successfully")
                onSuccess()
            }.addOnFailureListener {
                Log.e("Firestore", "Failed to remove workout: ${it.message}")
            }
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to load playlist for workout removal: ${it.message}")
        }
    }

    // Delete a playlist and remove its ID from the user's list
    fun removePlaylist(userId: String, playlistId: String, onSuccess: () -> Unit) {
        val playlistRef = playlistsCollection.document(playlistId)
        val userRef = db.collection("users").document(userId)

        db.runBatch { batch ->
            batch.delete(playlistRef)
            batch.update(userRef, "playlistIds", FieldValue.arrayRemove(playlistId))
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to remove playlist: ${it.message}")
        }
    }
}
