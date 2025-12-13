package com.example.habittracker.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
/**
 * Lưu lịch sử hoàn thành theo từng ngày
 * Dùng để tính streak, biểu đồ
 */
@Entity(
    tableName = "habit_history",
    indices = [Index(value = ["habitId", "date"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val habitId: Int,          // Thói quen nào

    val date: String,          // Định dạng yyyy-MM-dd

    val isCompleted: Boolean = false,  // Đã hoàn thành chưa
    val completedValue: Int? = null,   // Số lượng thực tế đạt trong ngày

    val createdAt: Long = System.currentTimeMillis()
)
