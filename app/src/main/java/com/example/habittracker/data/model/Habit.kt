package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userId: Int,           // ID người sở hữu thói quen
    val name: String,          // Tên thói quen
    val description: String? = null,  // Mô tả chi tiết

    val upNext: Int? = null,   // Số ngày muốn thực hiện liên tiếp
    val repeat: String = "Daily", // Lặp lại: Daily, Weekly, Custom
    val timeMode: String = "AnyTime", // AnyTime / SpecifiedTime
    val specifiedTime: Int? = null, // nếu timeMode = SpecifiedTime, lưu phút trong ngày (0-1439)

    val reminderMode: String = "None", // None / Custom
    val reminderTime: Int? = null,     // nếu reminderMode = Custom, lưu phút trong ngày

    val tag: String = "No tag", // Tag mặc định hoặc chọn từ danh sách
    // Ví dụ: "Morning Routine", "Workout", "Clean Room", "Healthy LifeStyle", "Sleep Better", "Relationship"

    val targetValue: Int? = null,     // Mục tiêu định lượng (VD: 2000)
    val targetUnit: String? = null,   // Đơn vị (ml, phút…)

    val color: String? = null,        // Màu hiển thị UI

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null,

    val isArchived: Boolean = false // Lưu trữ (ẩn thói quen)
)
