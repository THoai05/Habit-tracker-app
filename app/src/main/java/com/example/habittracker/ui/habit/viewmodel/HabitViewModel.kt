package com.example.habittracker.ui.habit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitHistory
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits

    // Lấy danh sách thói quen
    fun loadHabits(userId: Int) {
        viewModelScope.launch {
            _habits.value = repository.getHabits(userId)
        }
    }

    // Tạo thói quen
    fun addHabit(habit: Habit, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addHabit(habit)
            onComplete()
        }
    }

    // Cập nhật thói quen
    fun updateHabit(habit: Habit, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
            onComplete()
        }
    }

    // Xóa thói quen
    fun deleteHabit(habit: Habit, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            onComplete()
        }
    }

    // Thêm lịch sử hoàn thành
    fun addHabitHistory(history: HabitHistory, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addHabitHistory(history)
            onComplete()
        }
    }

    // Lấy lịch sử thói quen
    suspend fun getHabitHistory(habitId: Int): List<HabitHistory> {
        return repository.getHabitHistory(habitId)
    }
}
