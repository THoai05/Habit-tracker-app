package com.example.habittracker.ui.habit.list

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.habittracker.R
import com.example.habittracker.data.model.Habit
import com.example.habittracker.ui.habit.edit.EditHabitActivity
import com.google.android.material.button.MaterialButton
import android.widget.EditText
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.local.DatabaseProvider
import kotlinx.coroutines.launch

class HabitDetailDialogFragment(private val habit: Habit, private val onDelete: (Habit)->Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_habit_detail, null)

        val tvName = view.findViewById<TextView>(R.id.tvHabitDetailName)
        val tvRepeat = view.findViewById<TextView>(R.id.tvHabitDetailRepeat)
        val tvTime = view.findViewById<TextView>(R.id.tvHabitDetailTime)
        val tvReminder = view.findViewById<TextView>(R.id.tvHabitDetailReminder)
        val tvGoal = view.findViewById<TextView>(R.id.tvHabitDetailGoal)
        val btnEdit = view.findViewById<MaterialButton>(R.id.btnDetailEdit)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDetailDelete)
        val etNote = view.findViewById<EditText>(R.id.etHabitNote)
        etNote.setText(habit.note ?: "")

        tvName.text = habit.name
        tvRepeat.text = "Repeat: ${habit.repeat}"
        tvTime.text = "Time: ${habit.specifiedTime?.let { "${it/60}:${it%60}" } ?: "AnyTime"}"
        tvReminder.text = "Reminder: ${habit.reminderTime?.let { "${it/60}:${it%60}" } ?: "None"}"
        tvGoal.text = habit.targetValue?.let { "Goal: $it ${habit.targetUnit ?: ""}" } ?: "Goal: None"

        val dialog = Dialog(requireContext())
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.style.DialogSlideAnim

        btnEdit.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(requireContext(), EditHabitActivity::class.java)
            intent.putExtra("habitId", habit.id)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            dialog.dismiss()
            onDelete(habit)
        }

        val btnSaveNote = view.findViewById<Button>(R.id.btnSaveNote)
        btnSaveNote.setOnClickListener {
            habit.note = etNote.text.toString() // cập nhật field note trong Habit

            // Cập nhật database bằng Coroutine
            val db = DatabaseProvider.getDatabase(requireContext())
            lifecycleScope.launch {
                db.habitDao().updateHabit(habit)
            }
        }
        return dialog
    }
}
