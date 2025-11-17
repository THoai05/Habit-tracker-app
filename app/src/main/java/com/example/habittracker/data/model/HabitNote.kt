package com.example.habittracker.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
/**
 * Ghi chú ngắn mỗi lần user hoàn thành thói quen
 */
@Entity(
    tableName = "habit_notes",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val habitId: Int,        // Thuộc thói quen nào
    val note: String,        // Nội dung ghi chú
    val date: String,        // Ghi chú theo ngày

    val createdAt: Long = System.currentTimeMillis()
)
