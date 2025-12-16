package com.example.habittracker.ui.notifications

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.databinding.ActivityNotificationDetailBinding

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")

        binding.notificationDetailTitle.text = title
        binding.notificationDetailMessage.text = message

        // Thêm nút một cách linh hoạt dựa trên loại thông báo
        when (title) {
            "Habit Added" -> {
                addActionButton("View List") {
                    // TODO: Điều hướng đến danh sách thói quen
                }
            }
            "Habit Reminder" -> {
                addActionButton("Complete") {
                    // TODO: Đánh dấu thói quen là đã hoàn thành
                }
                addActionButton("Snooze") {
                    // TODO: Tạm ẩn thông báo
                }
            }
            "Habit Overdue" -> {
                addActionButton("Start Now") {
                    // TODO: Bắt đầu thực hiện thói quen
                }
                addActionButton("Skip") {
                    // TODO: Bỏ qua thói quen
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