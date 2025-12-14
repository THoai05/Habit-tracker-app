package com.example.habittracker.ui.habit.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.model.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onItemClick: (Habit) -> Unit,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit,
    private val onCheckClick: (Habit) -> Unit // 1. Thêm callback tick hoàn thành
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

        holder.tvHabitName.text = habit.name

        // --- Xử lý logic Hoàn thành (Không cho hoàn tác) ---
        if (habit.isCompletedToday) {
            // Nếu đã xong: Hiện màu xanh, alpha rõ, KHÔNG CHO CLICK NỮA
            holder.imgCheck.setImageResource(R.drawable.ic_check) // Icon tích V
            holder.imgCheck.background?.setTint(Color.parseColor("#4CAF50")) // Màu xanh lá
            holder.imgCheck.alpha = 1.0f
            holder.imgCheck.isEnabled = false // Khóa nút lại
        } else {
            // Nếu chưa xong: Màu xám mờ, cho phép click
            holder.imgCheck.setImageResource(0) // Xóa icon V đi (hoặc để icon tròn trống)
            holder.imgCheck.background?.setTint(Color.parseColor("#BDBDBD")) // Màu xám
            holder.imgCheck.alpha = 0.5f
            holder.imgCheck.isEnabled = true // Mở nút
        }

        // --- Xử lý sự kiện Click vào nút Check ---
        holder.imgCheck.setOnClickListener {
            // Hiệu ứng nảy lên xíu cho đẹp
            it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).duration = 100
            }.start()

            // Cập nhật UI ngay lập tức cho mượt (Optimistic update)
            habit.isCompletedToday = true
            notifyItemChanged(position)

            // Gọi ngược về Activity để lưu vào Database
            onCheckClick(habit)
        }

        // --- Các phần code cũ (Màu sắc, Animation) giữ nguyên ---
        try {
            holder.viewColor.setBackgroundColor(Color.parseColor(habit.color ?: "#FFD6E0"))
        } catch (e: Exception) {
            holder.viewColor.setBackgroundColor(Color.parseColor("#FFD6E0"))
        }

        val slideAnim = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up)
        holder.itemView.startAnimation(slideAnim)

        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.animate().scaleX(1.02f).scaleY(1.02f).setDuration(100).start()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
            false
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(habit)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(habit)
        }

        holder.itemView.setOnClickListener {
            onItemClick(habit)
        }
    }

    override fun getItemCount(): Int = habits.size

    // ... giữ nguyên removeItem, updateItem
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