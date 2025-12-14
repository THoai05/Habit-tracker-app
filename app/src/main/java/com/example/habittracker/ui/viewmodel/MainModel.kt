package com.example.habittracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitHistory
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(private val repository: HabitRepository) : ViewModel() {

    // Danh sách habit để hiển thị lên UI
    private val _displayHabits = MutableLiveData<List<Habit>>()
    val displayHabits: LiveData<List<Habit>> = _displayHabits

    // Ngày đang được chọn
    private var selectedDate: LocalDate = LocalDate.now()

    // Hàm load dữ liệu (gọi từ Activity)
    fun loadHabitsForDate(date: LocalDate) {
        selectedDate = date
        viewModelScope.launch {
            // 1. Lấy tất cả habit từ DB
            val allHabits = repository.getHabits(userId = 1) // TODO: Lấy userId thật từ Session

            // 2. Lọc và kiểm tra trạng thái hoàn thành
            val filteredList = processHabitList(allHabits, date)

            // 3. Đẩy dữ liệu ra UI
            _displayHabits.value = filteredList
        }
    }

    // Logic lọc và check history (Chuyển từ Activity sang đây)
    private suspend fun processHabitList(habits: List<Habit>, date: LocalDate): List<Habit> {
        val dateString = date.toString()

        // Bước lọc ngày tháng & UpNext
        val filtered = habits.filter { habit ->
            val createdDate = java.time.Instant.ofEpochMilli(habit.createdAt)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()

            // Chưa sinh ra thì chưa hiện
            if (date.isBefore(createdDate)) return@filter false

            // Check UpNext
            if (habit.upNext != null && habit.upNext > 0) {
                val endDate = createdDate.plusDays(habit.upNext.toLong())
                if (date.isBefore(endDate)) return@filter true else return@filter false
            }

            // Mặc định hiện (Daily, Weekly...)
            true
        }

        // Bước check database xem đã hoàn thành chưa
        filtered.forEach { habit ->
            val history = repository.getHistory(habit.id, dateString)
            habit.isCompletedToday = (history != null && history.isCompleted)
        }

        return filtered
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            // Reload lại list sau khi xóa
            loadHabitsForDate(selectedDate)
        }
    }

    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            val dateString = selectedDate.toString()

            // Check coi có chưa để tránh duplicate
            val existing = repository.getHistory(habit.id, dateString)

            if (existing == null) {
                val history = HabitHistory(
                    habitId = habit.id,
                    date = dateString,
                    isCompleted = true
                )
                repository.addHistory(history)

                // Cập nhật nhanh UI tại chỗ (Optimistic Update)
                // để không phải reload lại toàn bộ DB gây giật
                val currentList = _displayHabits.value?.toMutableList() ?: return@launch
                val index = currentList.indexOfFirst { it.id == habit.id }
                if (index != -1) {
                    currentList[index].isCompletedToday = true
                    _displayHabits.value = currentList
                }
            }
        }
    }
}