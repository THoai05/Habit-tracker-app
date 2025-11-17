package com.example.habittracker.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
/**
 * Lưu mục tiêu định lượng (ví dụ 2000 ml)
 */
@Entity(
    tableName = "habit_goals",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val habitId: Int,

    val targetValue: Int,      // Giá trị mục tiêu
    val targetUnit: String,    // Đơn vị

    val createdAt: Long = System.currentTimeMillis()
)
