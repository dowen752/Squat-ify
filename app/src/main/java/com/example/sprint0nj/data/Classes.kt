package com.example.sprint0nj.data

class Classes {
    data class Workout(
        val id: String = "",
        var title: String = "",
        var duration: String? = null,
        var reps: Int? = null,
        var sets: Int? = null,
        var description: String = "",
        var target: String = "",
        var tutorialLink: String = ""
    )

    data class Playlist(
        val id: String = "",
        val name: String = "",
        var workouts: MutableList<Workout> = mutableListOf()
    ) {
        fun addWorkout(workout: Workout) {
            workouts.add(workout)
        }

        fun removeWorkout(workout: Workout) {
            workouts.remove(workout)
        }
    }

    // Use firebaseAuth for password and authentification handling, doesnt need to be stored here
    data class User(
        val userId: String = "", // Will be a UUID on initialization
        val displayName: String = "", // Name or something
        val playlistIds: MutableList<String> = mutableListOf() // List of playlist UUIDs associated with the user
    )

}