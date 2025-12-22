package com.example.habittracker.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.MainActivity
import com.example.habittracker.databinding.ActivityNotificationDetailBinding
import com.example.habittracker.ui.habit.edit.EditHabitActivity

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val title = intent.getStringExtra("title") ?: ""
        val message = intent.getStringExtra("message") ?: ""

        binding.notificationDetailTitle.text = title
        binding.notificationDetailMessage.text = message

        // Xử lý các nút hành động dựa trên tiêu đề thông báo từ Database
        when {
            title.contains("Thêm thói quen thành công", ignoreCase = true) -> {
                addActionButton("Thêm hành động") {
                    val intent = Intent(this, EditHabitActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                addActionButton("Về trang chủ") {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
            title.contains("Đã hoàn thành", ignoreCase = true) -> {
                addActionButton("Xem danh sách") {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
            title.contains("Sắp đến giờ", ignoreCase = true) -> {
                addActionButton("Thực hiện ngay") {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
            title.contains("Quá hạn", ignoreCase = true) -> {
                addActionButton("Bắt đầu ngay") {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun addActionButton(text: String, onClick: () -> Unit) {
        val button = Button(this).apply {
            this.text = text
            setOnClickListener { onClick() }
        }
        binding.buttonContainer.addView(button)
    }
}