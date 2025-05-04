package com.example.sprint0nj

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkoutTimerViewModel : ViewModel() {
    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private var timerJobStarted = false

    fun startTimer() {
        _isTimerRunning.value = true
        if (!timerJobStarted) {
            timerJobStarted = true
            viewModelScope.launch {
                while (true) {
                    if (_isTimerRunning.value) {
                        delay(1000)
                        _timerSeconds.value += 1
                    } else {
                        delay(100)
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
    }
}