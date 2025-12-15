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
    fun loadHabits(userId: Int, date: String) { // Thêm tham số date
        viewModelScope.launch {
            val habitList = repository.getHabits(userId)

            habitList.forEach { habit ->
                // Check xem ngày ĐÓ (date) đã làm chưa
                habit.isCompletedToday = repository.isCompletedOnDate(habit.id, date)
                // Tính streak (Logic tính streak vẫn giữ nguyên)
                habit.currentStreak = repository.calculateAndGetStreak(habit.id)
            }
            _habits.value = habitList
        }
    }

    // Tạo thói quen
    fun addHabit(habit: Habit, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
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
    fun toggleHabit(habit: Habit, date: String) { // Thêm tham số date
        viewModelScope.launch {
            // Gọi repo với ngày cụ thể
            repository.toggleHabit(habit.id, date)

            val newStreak = repository.calculateAndGetStreak(habit.id)
            val isCompleted = repository.isCompletedOnDate(habit.id, date)

            // Update UI
            val updatedList = _habits.value.map {
                if (it.id == habit.id) {
                    it.copy().apply {
                        this.isCompletedToday = isCompleted
                        this.currentStreak = newStreak
                    }
                } else {
                    it
                }
            }
            _habits.value = updatedList
        }
    }
    fun getStreak(habitId: Int) {
        viewModelScope.launch {
            // Gọi repository tính toán
            val streak = repository.calculateAndGetStreak(habitId)

            // Log ra check thử hoặc update vào LiveData/StateFlow để UI hiển thị
            println("Streak của Habit $habitId là: $streak 🔥")

            // Ví dụ: _uiState.value = _uiState.value.copy(currentStreak = streak)
        }
    }
}
