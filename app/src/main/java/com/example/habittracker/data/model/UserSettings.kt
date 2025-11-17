package com.example.habittracker.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
/**
 * Lưu cài đặt cá nhân: dark mode, thông báo, ngôn ngữ
 */
@Entity(
    tableName = "user_settings",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserSettings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userId: Int,            // Thuộc người dùng nào

    val darkMode: Boolean = false,
    val notificationEnabled: Boolean = true,
    val locale: String = "vi",

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
