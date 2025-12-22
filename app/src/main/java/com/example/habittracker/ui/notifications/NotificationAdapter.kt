package com.example.habittracker.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.model.NotificationEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(
    private var notifications: List<NotificationEntity>,
    private val onItemClick: (NotificationEntity) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    fun updateData(newNotifications: List<NotificationEntity>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        
        // Chọn icon dựa trên type
        val iconRes = when (notification.type) {
            "ADD" -> R.drawable.ic_notifications
            "COMPLETE" -> R.drawable.ic_notifications
            else -> R.drawable.ic_notifications
        }
        
        holder.icon.setImageResource(iconRes)
        holder.title.text = notification.title
        holder.message.text = notification.message
        
        // Định dạng thời gian
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        holder.time.text = sdf.format(Date(notification.timestamp))
        
        holder.itemView.setOnClickListener { onItemClick(notification) }
    }

    override fun getItemCount() = notifications.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.notification_icon)
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val message: TextView = itemView.findViewById(R.id.notification_message)
        val time: TextView = itemView.findViewById(R.id.notification_time)
    }
}