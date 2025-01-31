package com.example.phone.Adapter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.phone.Model.CallLogItem
import com.example.phone.R

class CallLogAdapter(private val callLogs: List<CallLogItem>, private val context: Context) :
    RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    class CallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameOrNumber: TextView = itemView.findViewById(R.id.tvNameOrNumber)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val btnCall: ImageView = itemView.findViewById(R.id.btnCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_call_log, parent, false)
        return CallLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        val callLog = callLogs[position]
        holder.tvNameOrNumber.text = callLog.contactName ?: callLog.number
        holder.tvType.text = "Type: ${callLog.type}"
        holder.tvDate.text = "Date: ${callLog.date}"
        holder.tvDuration.text = "Duration: ${callLog.duration}"

        // Handle call button click
        holder.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${callLog.number}"))
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Call permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = callLogs.size
}