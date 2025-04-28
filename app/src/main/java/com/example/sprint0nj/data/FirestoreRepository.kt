package com.example.sprint0nj.data

import android.util.Log
import com.example.sprint0nj.data.Classes.Playlist
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.sprint0nj.data.Classes.Workout
import com.google.firebase.auth.FirebaseAuth

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
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Workout::class.java)?.copy(id = doc.id)
            }
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
    
    
    // Username and password are n o t saved in firestore. That info is stored in auth. When looking for users, use displayName to filter.
    fun postUser(username: String, password: String, displayName: String, onSuccess: () -> Unit,  onFailure: () -> Unit){
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
                            onFailure()
                        }
                } else{
                    onFailure()
                    Log.d("Auth UID", "UID brokie")
                }
            }
            .addOnFailureListener { error ->
                Log.d("Auth Auth", "Authentication brokie: ${error.message}")
                onFailure()
            }
    }

    // BRO JUST DO FETCHPLAYLISTBYID -> POSTPLAYLIST WITH A NEW UUID AND THEN SHARE THAT LIKE BRUH
    fun sharePlaylist(destUsername: String, playlistId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val usersCollection = db.collection("users")

        // Fetch the destination user
        usersCollection.whereEqualTo("displayName", destUsername)
            .get()
            .addOnSuccessListener { userSnapshot ->
                if (!userSnapshot.isEmpty) {
                    val destUser = userSnapshot.documents.first()
                    val destUserId = destUser.id

                    //  Fetch the original playlist
                    playlistsCollection.document(playlistId).get()
                        .addOnSuccessListener { playlistDoc ->
                            val originalPlaylist = playlistDoc.toObject(Classes.Playlist::class.java)
                            if (originalPlaylist != null) {
                                // Create copy of the playlist with a new UUID
                                val newPlaylist = originalPlaylist.copy(
                                    id = db.collection("playlists").document().id, // Easier way to do UUID ofc I learn this now
                                    name = "Copy of ${originalPlaylist.name}")

                                // Post new playlist
                                postPlaylist(newPlaylist, destUserId) {
                                    // Add the new playlist ID to the destination user's playlistIds
                                    usersCollection.document(destUserId)
                                        .update("playlistIds", FieldValue.arrayUnion(newPlaylist.id))
                                        .addOnSuccessListener { onSuccess() }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Failed to update user playlist list: ${e.message}")
                                            onFailure()
                                        }
                                }
                            } else {
                                Log.e("Firestore", "Original playlist not found")
                                onFailure()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Failed to fetch original playlist: ${e.message}")
                            onFailure()
                        }

                } else {
                    Log.e("Firestore", "Destination user not found")
                    onFailure()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch destination user: ${e.message}")
                onFailure()
            }
    }

    // Needs to get used for displaying friends list
    suspend fun fetchUser(userId: String): Classes.User? {
        return try{
            val user = db.collection("Users").document(userId).get().await()
            if(user.exists()){
                user.toObject(Classes.User::class.java)
            }
            else{
                null
            }
        }
        catch(e: Exception){
            Log.e("Firestore", "Error fetching user: ${e.message}")
            null
        }
    }

// Takes all data from one user and imposes it on another, not really needed unless switching usernames and passwords
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
