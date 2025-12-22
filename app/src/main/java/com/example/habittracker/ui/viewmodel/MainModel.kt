package com.example.habittracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(private val repository: HabitRepository) : ViewModel() {

    // Danh sách habit hiển thị
    private val _displayHabits = MutableLiveData<List<Habit>>()
    val displayHabits: LiveData<List<Habit>> = _displayHabits

    // Lưu ngày đang chọn (Dùng String để đồng bộ với Activity)
    private var selectedDate: String = LocalDate.now().toString()

    // --- 1. Hàm set ngày (Đã lôi ra ngoài cho đúng cú pháp) ---
    fun setCurrentSelectedDate(date: String) {
        this.selectedDate = date
        loadHabitsForDate(date)
    }

    // --- 2. Hàm load dữ liệu (Nhận vào String) ---
    fun loadHabitsForDate(dateString: String) {
        // Cập nhật lại biến selectedDate để chắc chắn
        this.selectedDate = dateString

        viewModelScope.launch {
            // Lấy tất cả habit
            val allHabits = repository.getHabits(userId = 1)

            // Chuyển String sang LocalDate để tính toán logic ngày tháng (createdAt, upNext)
            val parsedDate = LocalDate.parse(dateString)

            // Xử lý lọc và check trạng thái
            val filteredList = processHabitList(allHabits, parsedDate, dateString)

            _displayHabits.value = filteredList
        }
    }

    // Logic lọc, check history VÀ tính Streak
    private suspend fun processHabitList(habits: List<Habit>, dateCalc: LocalDate, dateString: String): List<Habit> {
        // Bước 1: Lọc những habit chưa đến ngày sinh ra hoặc đã hết hạn
        val filtered = habits.filter { habit ->
            val createdDate = java.time.Instant.ofEpochMilli(habit.createdAt)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()

            if (dateCalc.isBefore(createdDate)) return@filter false

            if (habit.upNext != null && habit.upNext > 0) {
                val endDate = createdDate.plusDays(habit.upNext.toLong())
                if (dateCalc.isBefore(endDate)) return@filter true else return@filter false
            }
            true
        }

        // Bước 2: Check trạng thái hoàn thành & Tính Streak
        filtered.forEach { habit ->
            // Check xem ngày này (dateString) đã làm chưa
            habit.isCompletedToday = repository.isCompletedOnDate(habit.id, dateString)

            // Tính chuỗi streak hiện tại (lấy từ cache hoặc tính lại)
            habit.currentStreak = repository.calculateAndGetStreak(habit.id)
        }

        return filtered
    }

    // --- 3. Hàm xử lý Check/Uncheck (Dùng toggle cho xịn) ---
    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            // Gọi toggle trong Repository (tự động thêm hoặc xóa)
            repository.toggleHabit(habit.id, selectedDate)

            // Sau khi toggle xong, tính lại trạng thái mới để update UI
            val isCompletedNow = repository.isCompletedOnDate(habit.id, selectedDate)
            val newStreak = repository.calculateAndGetStreak(habit.id)

            // Cập nhật nhanh UI (Optimistic Update)
            val currentList = _displayHabits.value?.map {
                if (it.id == habit.id) {
                    it.copy().apply {
                        this.isCompletedToday = isCompletedNow
                        this.currentStreak = newStreak
                    }
                } else {
                    it
                }
            }
            _displayHabits.value = currentList!!
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            loadHabitsForDate(selectedDate)
        }
    }
}