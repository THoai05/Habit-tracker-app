package com.example.habittracker.data.model
/**
 * Bảng Users: Lưu thông tin người dùng
 * Dùng cho đăng ký / đăng nhập / thiết lập cá nhân
 */
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val name: String,          // Tên người dùng
    val email: String,         // Email đăng nhập
    val password: String,      // Mật khẩu (nên mã hoá)

    val createdAt: Long = System.currentTimeMillis()
)

