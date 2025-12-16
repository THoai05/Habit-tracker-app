package com.example.habittracker.ui.notifications

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.R
import com.example.habittracker.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val notifications = listOf(
            Notification(R.drawable.ic_notifications, "Habit Added", "You have added a new habit: Reading.", "10:00 AM"),
            Notification(R.drawable.ic_notifications, "Habit Completed", "You have completed your habit: Meditation.", "11:30 AM"),
            Notification(R.drawable.ic_notifications, "Habit Reminder", "It's time to complete your habit: Exercise.", "1:00 PM"),
            Notification(R.drawable.ic_notifications, "Habit Overdue", "You have missed your habit: Journaling.", "2:15 PM")
        )

        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notificationsRecyclerView.adapter = NotificationAdapter(notifications) { notification ->
            val intent = Intent(this, NotificationDetailActivity::class.java).apply {
                putExtra("title", notification.title)
                putExtra("message", notification.message)
            }
            startActivity(intent)
        }
    }
}
