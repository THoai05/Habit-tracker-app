package com.example.habittracker.ui.habit.manage
import com.example.habittracker.data.repository.HabitRepository
import androidx.lifecycle.ViewModel
import com.example.habittracker.data.model.Habit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ManageHabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> = _habits

    init {
        loadHabits()
    }

    fun loadHabits() {
        viewModelScope.launch {
            _habits.value = repository.getAllHabits() // <-- sửa repo nếu cần
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            loadHabits() // reload sau khi xóa
        }
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            val today = com.example.habittracker.utils.DateUtils.getCurrentDate() // format "yyyy-MM-dd"
            repository.toggleHabit(habit.id, today)
            loadHabits() // reload dữ liệu sau khi toggle
        }
    }
}
