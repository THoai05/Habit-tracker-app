package com.example.habittracker.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.NotificationEntity
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.data.repository.NotificationRepository
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

class NotificationsViewModel(
    private val notificationRepo: NotificationRepository,
    private val habitRepo: HabitRepository,
    private val sessionManager: NotificationSessionManager // Thêm SessionManager
) : ViewModel() {

    private val _notifications = MutableLiveData<List<NotificationEntity>>()
    val notifications: LiveData<List<NotificationEntity>> = _notifications

    fun fetchNotifications() {
        viewModelScope.launch {
            checkAndTimeBasedNotifications()
            _notifications.value = notificationRepo.getAllNotifications()
        }
    }

    private suspend fun checkAndTimeBasedNotifications() {
        // LẤY USER ID THẬT TỪ SESSION MANAGER
        val currentUserId = sessionManager.getUserId()
        
        val habits = habitRepo.getHabits(currentUserId)
        val now = LocalTime.now()
        val today = LocalDate.now().toString()

        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        habits.forEach { habit ->
            habit.reminderTime?.let { timeInMinutes ->
                val reminderTime = LocalTime.of(timeInMinutes / 60, timeInMinutes % 60)
                val isCompleted = habitRepo.isCompletedOnDate(habit.id, today)

                if (!isCompleted) {
                    val duration = Duration.between(now, reminderTime)
                    val minutesUntil = duration.toMinutes()

                    // Nhắc sớm (Upcoming)
                    if (minutesUntil in 0..5) {
                        val existing = notificationRepo.getRecentNotification(habit.id, "UPCOMING", startOfDay)
                        if (existing == null) {
                            notificationRepo.addNotification(
                                NotificationEntity(
                                    title = "Sắp đến giờ thực hiện!",
                                    message = "Đã gần đến lúc bạn cần thực hiện: ${habit.name}. Hãy chuẩn bị nhé!",
                                    type = "UPCOMING",
                                    habitId = habit.id
                                )
                            )
                        }
                    }
                    
                    // Báo trễ (Overdue)
                    if (now.isAfter(reminderTime.plusMinutes(1))) {
                        val existingOverdue = notificationRepo.getRecentNotification(habit.id, "OVERDUE", startOfDay)
                        if (existingOverdue == null) {
                            notificationRepo.addNotification(
                                NotificationEntity(
                                    title = "Thói quen đang bị trễ!",
                                    message = "Bạn đã trễ giờ thực hiện thói quen: ${habit.name}. Hãy hoàn thành ngay để duy trì tiến độ nhé!",
                                    type = "OVERDUE",
                                    habitId = habit.id
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}