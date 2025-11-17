package com.example.habittracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.model.Habit
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = DatabaseProvider.getDatabase(this)
        val habitDao = db.habitDao()

        // Thêm 1 thói quen test để database được tạo
        lifecycleScope.launch {
            habitDao.insertHabit(
                Habit(
                    userId = 1,
                    name = "Uống nước",
                    upNext = 7,              // thực hiện 7 ngày liên tiếp
                    repeat = "Daily",        // Daily / Weekly / Custom
                    timeMode = "AnyTime",    // AnyTime hoặc SpecifiedTime
                    specifiedTime = null,    // nếu dùng SpecifiedTime
                    reminderMode = "Custom",
                    reminderTime = 8 * 60,   // 8 giờ sáng
                    tag = "Healthy LifeStyle",
                    targetValue = 2000,
                    targetUnit = "ml"
                )
            )
        }

    }
}