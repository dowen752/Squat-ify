package com.example.sprint0nj.data

class Classes {
    data class Workout(
        val id: Int = 0,
        var title: String = "",
        var duration: String? = null,
        var reps: Int? = null,
        var sets: Int? = null,
        var description: String = ""
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
    object WorkoutMods { // Using this instead of normal initialization means we can pass these values as parameters
        fun addWorkout(           // instead of needing to identify each value
            id: Int,
            title: String,
            duration: String? = null,
            reps: Int? = null,
            sets: Int? = null,
            description: String = ""
        ): Workout {
            return Classes.Workout(id, title, duration, reps, sets, description)
        }
    }
}
//    val actWorkout = listOf(
//        Workout(1, "Full Body HIIT", "30 min", "Intermediate"),
//        Workout(2, "Yoga Flow", "45 min", "Beginner"),
//        Workout(3, "Strength Training", "40 min", "Advanced"),
//        Workout(4, "Cardio Blast", "20 min", "Intermediate"),
//        Workout(5, "Core Burner", "25 min", "Intermediate"),
//        Workout(6, "Leg Day", "50 min", "Advanced"),
//        Workout(7, "Upper Body Strength", "35 min", "Intermediate")
//    )
//}