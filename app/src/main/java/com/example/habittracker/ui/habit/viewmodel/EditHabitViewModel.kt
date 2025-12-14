package com.example.habittracker.ui.habit.viewmodel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.launch

class EditHabitViewModel(private val repository: HabitRepository) : ViewModel() {

    // --- State Variables (Dữ liệu hiển thị lên UI) ---
    val habitName = MutableLiveData("")
    val selectedColor = MutableLiveData(Color.parseColor("#FFD6E0"))

    val upNext = MutableLiveData<Int?>(null)
    val repeat = MutableLiveData("Daily")
    val timeMode = MutableLiveData("AnyTime")
    val specifiedTime = MutableLiveData<Int?>(null) // Phút (ex: 12:30 -> 750)
    val reminderMode = MutableLiveData("None")
    val reminderTime = MutableLiveData<Int?>(null)
    val tag = MutableLiveData("No tag")
    val targetValue = MutableLiveData<Int?>(null)
    val targetUnit = MutableLiveData<String?>(null)

    // Trạng thái lưu thành công để Activity biết mà chuyển màn hình
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private var currentHabitId: Int = -1

    // Load dữ liệu khi Edit
    fun loadHabit(id: Int) {
        if (id == -1) return
        currentHabitId = id
        viewModelScope.launch {
            val habit = repository.getHabitById(id)
            habit?.let {
                habitName.value = it.name
                selectedColor.value = Color.parseColor(it.color)
                repeat.value = it.repeat
                upNext.value = it.upNext
                timeMode.value = it.timeMode
                specifiedTime.value = it.specifiedTime
                reminderMode.value = it.reminderMode
                reminderTime.value = it.reminderTime
                tag.value = it.tag
                targetValue.value = it.targetValue
                targetUnit.value = it.targetUnit
            }
        }
    }

    fun saveHabit(name: String) {
        if (name.isBlank()) return // Activity đã check rồi nhưng check lại cho chắc

        viewModelScope.launch {
            // Chuyển màu Int sang Hex String
            val colorHex = String.format("#%06X", 0xFFFFFF and (selectedColor.value ?: Color.WHITE))

            val habit = Habit(
                id = if (currentHabitId == -1) 0 else currentHabitId,
                userId = 1, // TODO: Lấy ID từ User Session thật
                name = name,
                repeat = repeat.value ?: "Daily",
                upNext = upNext.value,
                timeMode = timeMode.value ?: "AnyTime",
                specifiedTime = specifiedTime.value,
                reminderMode = reminderMode.value ?: "None",
                reminderTime = reminderTime.value,
                tag = tag.value ?: "No tag",
                targetValue = targetValue.value,
                targetUnit = targetUnit.value,
                color = colorHex
            )

            if (currentHabitId == -1) {
                repository.insertHabit(habit)
            } else {
                repository.updateHabit(habit)
            }
            _saveSuccess.value = true
        }
    }

    // Các hàm update value từ Dialog
    fun updateColor(color: Int) { selectedColor.value = color }
    fun updateUpNext(value: Int) { upNext.value = value }
    fun updateRepeat(value: String) { repeat.value = value }
    fun updateTag(value: String) { tag.value = value }

    fun updateTime(mode: String, time: Int?) {
        timeMode.value = mode
        specifiedTime.value = time
    }

    fun updateReminder(mode: String, time: Int?) {
        reminderMode.value = mode
        reminderTime.value = time
    }

    fun updateGoal(value: Int, unit: String) {
        targetValue.value = value
        targetUnit.value = unit
    }
}