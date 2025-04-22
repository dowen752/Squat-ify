package com.example.sprint0nj.data

import com.google.firebase.firestore.DocumentId

data class Workout(
    @DocumentId
    var id: String = "",
    var title: String = "",
    var duration: String? = null,
    var reps: Int? = null,
    var sets: Int? = null,
    var description: String = "",
    var target: String = "",
    var tutorialLink: String = ""
)
