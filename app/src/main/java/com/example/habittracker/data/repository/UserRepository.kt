package com.example.habittracker.data.repository

import com.example.habittracker.data.local.dao.UserDao
import com.example.habittracker.data.model.User
import com.example.habittracker.utils.SecurityUtils

class UserRepository(private val userDao: UserDao) {

    // Đăng ký
    suspend fun registerUser(user: User): Result<Long> {
        return try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Result.failure(Exception("Email đã tồn tại!"))
            } else {
                // Mã hóa pass trước khi lưu
                val secureUser = user.copy(password = SecurityUtils.hashPassword(user.password))
                val id = userDao.insertUser(secureUser)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Đăng nhập
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)
            if (user != null) {
                // So sánh hash của pass nhập vào với hash trong DB
                if (user.password == SecurityUtils.hashPassword(password)) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Mật khẩu không đúng!"))
                }
            } else {
                Result.failure(Exception("Tài khoản không tồn tại!"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}