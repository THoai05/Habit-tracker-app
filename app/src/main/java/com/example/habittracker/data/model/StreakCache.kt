package com.example.habittracker.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
/**
 * Cache streak để không phải tính lại mỗi lần vào app
 */
@Entity(
    tableName = "streak_cache",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StreakCache(
    @PrimaryKey val habitId: Int,

    val currentStreak: Int,
    val longestStreak: Int,

    val lastCheckedDate: String // yyyy-MM-dd
)
