package com.example.sprint0nj

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprint0nj.data.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.sprint0nj.data.Classes.Workout

class WorkoutViewModel : ViewModel() {

    private val repository = FirestoreRepository()
    private val _workout = MutableStateFlow<Workout?>(null)
    val workout: StateFlow<Workout?> = _workout

    fun loadWorkout(workoutId: String) {
        _workout.value = null
        viewModelScope.launch {
            try {
                val fetchedWorkout = repository.fetchWorkoutById(workoutId)
                if (fetchedWorkout != null) {
                    _workout.value = fetchedWorkout
                } else {
                    Log.e("WorkoutViewModel", "Workout not found for ID: $workoutId")
                    _workout.value = null
                }
            } catch (e: Exception) {
                Log.e("WorkoutViewModel", "Error fetching workout: ${e.message}")
                _workout.value = null
            }
        }
    }
}
