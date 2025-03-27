package com.example.squat_ifyplaylists

data class Workout(
    val id: Int,
    var title: String,
    var duration: String,
    var difficulty: String,
    var reps: Int = 0,
    var sets: Int = 0,
    var description: String = ""
)
data class Playlist(
    val id: Int,
    var name: String,
    var workouts: MutableList<Workout>)
{
    fun addWorkout(workout: Workout) {
        workouts.add(workout)
    }
    fun removeWorkout(workout: Workout) {
        workouts.remove(workout)
    }
}
val actWorkout = listOf(
    Workout(1, "Full Body HIIT", "30 min", "Intermediate"),
    Workout(2, "Yoga Flow", "45 min", "Beginner"),
    Workout(3, "Strength Training", "40 min", "Advanced"),
    Workout(4, "Cardio Blast", "20 min", "Intermediate"),
    Workout(5, "Core Burner", "25 min", "Intermediate"),
    Workout(6, "Leg Day", "50 min", "Advanced"),
    Workout(7, "Upper Body Strength", "35 min", "Intermediate")
)