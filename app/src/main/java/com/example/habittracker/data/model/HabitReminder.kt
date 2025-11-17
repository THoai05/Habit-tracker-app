package com.example.habittracker.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
/**
 * Thời gian nhắc nhở cho mỗi thói quen
 */
@Entity(
    tableName = "habit_reminders",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val habitId: Int,

    val time: String,          // HH:mm
    val isEnabled: Boolean = true
)
