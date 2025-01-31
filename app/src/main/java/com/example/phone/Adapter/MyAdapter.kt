package com.example.phone.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phone.Model.Contact
import com.example.phone.R

class MyAdapter(private val contactList: List<Contact>): RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    private var filteredList: List<Contact> = contactList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.contact_name)
        val phone: TextView = itemView.findViewById(R.id.contact_phone)
        val callButton: ImageView = itemView.findViewById(R.id.call_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = filteredList[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone

        holder.callButton.setOnClickListener {
            val phoneNumber = contact.phone
            makeCall(holder.itemView.context, phoneNumber)
        }
    }

    private fun makeCall(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            contactList
        } else {
            contactList.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}