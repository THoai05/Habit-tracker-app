package com.example.habittracker.ui.habit.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.model.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit,
    private val onCheckClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCheck: ImageView = itemView.findViewById(R.id.imgCheck)
        val tvHabitName: TextView = itemView.findViewById(R.id.tvHabitName)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val viewColor: View = itemView.findViewById(R.id.viewColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        // --- Hiển thị tên ---
        holder.tvHabitName.text = habit.name

        // --- Hiển thị trạng thái hoàn thành ---
        if (habit.isCompletedToday) {
            holder.imgCheck.setImageResource(R.drawable.ic_check)
            holder.imgCheck.background?.setTint(Color.parseColor("#4CAF50"))
            holder.imgCheck.alpha = 1f
            holder.imgCheck.isEnabled = false
        } else {
            holder.imgCheck.setImageResource(0)
            holder.imgCheck.background?.setTint(Color.parseColor("#BDBDBD"))
            holder.imgCheck.alpha = 0.5f
            holder.imgCheck.isEnabled = true
        }

        // --- Click vào imgCheck để hoàn thành ---
        holder.imgCheck.setOnClickListener {
            it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).duration = 100
            }.start()

            habit.isCompletedToday = true
            notifyItemChanged(position)
            onCheckClick(habit)
        }

        // --- Click Edit/Delete ---
        holder.btnEdit.setOnClickListener { onEditClick(habit) }
        holder.btnDelete.setOnClickListener { onDeleteClick(habit) }

        // --- Click Card → show dialog ---
        holder.itemView.setOnClickListener {
            val fragment = HabitDetailDialogFragment(
                habit = habit,
                onDelete = { habitToDelete ->
                    onDeleteClick(habitToDelete)
                }
            )
            val activity = holder.itemView.context as? AppCompatActivity
            activity?.supportFragmentManager?.let { fm ->
                fragment.show(fm, "HabitDetail")
            }
        }

        // --- Màu sắc viewColor ---
        try {
            holder.viewColor.setBackgroundColor(Color.parseColor(habit.color ?: "#FFD6E0"))
        } catch (e: Exception) {
            holder.viewColor.setBackgroundColor(Color.parseColor("#FFD6E0"))
        }

        // --- Animation slide lên ---
        val slideAnim = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up_fade)
        holder.itemView.startAnimation(slideAnim)

        // --- Touch effect: scale card khi nhấn ---
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.animate().scaleX(1.02f).scaleY(1.02f)
                    .setDuration(100).start()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.animate().scaleX(1f)
                    .scaleY(1f).setDuration(100).start()
            }
            false
        }
    }

    override fun getItemCount(): Int = habits.size

    // --- Các hàm tiện ích ---
    fun removeItem(habit: Habit) {
        val index = habits.indexOf(habit)
        if (index != -1) {
            habits.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateItem(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            notifyItemChanged(index)
        }
    }

    fun updateList(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}
