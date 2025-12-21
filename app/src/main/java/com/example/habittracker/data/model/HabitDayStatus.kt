package com.example.habittracker.data.model

import androidx.room.Embedded

data class HabitDayStatus(
    @Embedded val habit: Habit,
    val isCompleted: Boolean, // Lấy từ bảng history (true nếu tìm thấy, false nếu null)
    val historyDate: String?  // Ngày hoàn thành (nếu có)
)