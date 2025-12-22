package com.example.habittracker.ui.notifications

import android.content.Context
import android.content.SharedPreferences

class NotificationSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("notification_settings_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "pref_user_id"
    }

    // Hàm này giúp bạn đặt ID để test mà không cần sửa code Login
    fun setUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int {
        // Mặc định trả về 1 nếu chưa được set, để app không bị crash
        return prefs.getInt(KEY_USER_ID, 1)
    }
}