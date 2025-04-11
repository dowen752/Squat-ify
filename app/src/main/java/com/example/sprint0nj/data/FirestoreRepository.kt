package com.example.sprint0nj.data
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sprint0nj.data.Classes.Playlist
import com.example.sprint0nj.data.Classes.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val playlistsCollection = db.collection("playlists")

    // Fetching pair of all playlist ids and names, for displaying in library screen
    fun fetchPlaylistSummaries(userId: String, onResult: (List<Pair<String, String>>) -> Unit) {
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val playlistIds = document.get("playlistIds") as? List<String> ?: emptyList() // If null, empty list

                if (playlistIds.isEmpty()) { // Handling edge cases
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                db.collection("playlists") // Searching playlists collection with list of user's playlist ids
                    .whereIn(FieldPath.documentId(), playlistIds)
                    .get()
                    .addOnSuccessListener { summaryQuery ->
                        val summaries = summaryQuery.documents.mapNotNull { doc -> // mapping playlist summaries
                            val name = doc.getString("name")
                            if (name != null) doc.id to name else null
                        }
                        onResult(summaries)
                    }
            } else {
                onResult(emptyList())  // No user doc = no playlists
            }
        }.addOnFailureListener {
            onResult(emptyList())  // In case of any error
        }
    }



    // Function to add a playlist to Firestore using provided playlist
    fun postPlaylist(playlist: Playlist, userId: String,  onSuccess: () -> Unit = {}) {
        val playlistRef = playlistsCollection.document(playlist.id)
        val userRef = db.collection("users").document(userId)

        db.runBatch { batch ->
            // Save the playlist
            batch.set(playlistRef, playlist)

            // Add this playlist ID to the user's list
            batch.update(userRef, "playlistIds", FieldValue.arrayUnion(playlist.id))

        }.addOnSuccessListener {
            onSuccess()
        }
    }


    // Fetches playlist by specified id. Used for displaying individual workout pages
    suspend fun fetchPlaylist(playlistId: String): Playlist? {
        return try {
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
        onSuccess: () -> Unit = {} // On success, needs to then update UI and give toast message
    ) {
        val playlist = playlistsCollection.document(playlistId)

        playlist.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val playlistObj = document.toObject(Playlist::class.java)
                val updatedWorkouts = playlistObj?.workouts?.filter { it.id != workoutId }

                playlist.update("workouts", updatedWorkouts)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Workout removed successfully")
                        onSuccess()
                    }
            }
        }
    }


    fun removePlaylist(userId: String, playlistId: String, onSuccess: () -> Unit) {

        // Reference to the playlist document
        val playlistRef = playlistsCollection.document(playlistId)
        val userRef = db.collection("users").document(userId) // Reference to the user document

        db.runBatch { batch ->
            // Delete the playlist document
            batch.delete(playlistRef)

            // Update the user's playlist list by removing this playlist ID
            batch.update(userRef, "playlistIds", FieldValue.arrayRemove(playlistId))
        }.addOnSuccessListener {
            onSuccess()
        }
    }


    fun postUser(username: String, password: String, displayName: String, onSuccess: () -> Unit){
        val fakeImposterEmail = "${username}@squatify.com"
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(fakeImposterEmail, password)
            .addOnSuccessListener{ result->
                val uid = result.user?.uid
                if(uid != null){
                    val newUser = Classes.User(
                        userId = uid,
                        displayName = displayName,
                        playlistIds = mutableListOf("c1f35457-a0ab-43eb-a38a-0f738bdac29d")
                    )

                    db.collection("users").document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { firestoreError ->
                            Log.d("Firestore", "setting document no workie: ${firestoreError.message}")
                        }
                } else{
                    Log.d("Auth UID", "UID brokie")
                }
            }
            .addOnFailureListener { error ->
                Log.d("Auth Auth", "Authentication brokie: ${error.message}")
            }
    }

    fun switchingUsers(){
        val newUid = FirebaseAuth.getInstance().currentUser?.uid
        val legacyUid = "4dz7wUNpKHI0Br9lSg9o" // your test UID

        val users = db.collection("users")
        users.document(newUid!!).get().addOnSuccessListener { newDoc ->
            if (!newDoc.exists()) {
                users.document(legacyUid).get().addOnSuccessListener { legacyDoc ->
                    if (legacyDoc.exists()) {
                        users.document(newUid).set(legacyDoc.data!!)
                    }
                }
            }
        }
    }


}
