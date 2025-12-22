package com.example.habittracker.ui.notifications

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.data.repository.NotificationRepository
import com.example.habittracker.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var viewModel: NotificationsViewModel
    private lateinit var adapter: NotificationAdapter

    class NotificationsViewModelFactory(
        private val notificationRepo: NotificationRepository,
        private val habitRepo: HabitRepository,
        private val sessionManager: NotificationSessionManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotificationsViewModel(notificationRepo, habitRepo, sessionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupViewModel()
        setupRecyclerView()
        observeViewModel()
        
        viewModel.fetchNotifications()
    }

    private fun setupViewModel() {
        val db = DatabaseProvider.getDatabase(this)
        val notificationRepo = NotificationRepository(db.notificationDao())
        val habitRepo = HabitRepository(db.habitDao(), db.habitHistoryDao(), db.streakCacheDao(), db.notificationDao())
        
        // Khởi tạo SessionManager riêng của bạn
        val sessionManager = NotificationSessionManager(this)
        
        val factory = NotificationsViewModelFactory(notificationRepo, habitRepo, sessionManager)
        viewModel = ViewModelProvider(this, factory)[NotificationsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(emptyList()) { notification ->
            val intent = Intent(this, NotificationDetailActivity::class.java).apply {
                putExtra("title", notification.title)
                putExtra("message", notification.message)
            }
            startActivity(intent)
        }
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notificationsRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.notifications.observe(this) { notifications ->
            adapter.updateData(notifications)
        }
    }
}