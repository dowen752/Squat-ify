package com.example.sprint0nj.data
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sprint0nj.data.Classes.Playlist

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val playlistsCollection = db.collection("playlists")

    // Function to add a playlist to Firestore using provided playlist
    fun addPlaylist(playlist: Playlist, callback: (Boolean, String?) -> Unit) {
        playlistsCollection.document(playlist.id).set(playlist) // sets document w/ id "document.id"to value of playlist
            .addOnSuccessListener {
                callback(true, null) // If successful, continue
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message) // If fails, return error message and false
            }
    }


}