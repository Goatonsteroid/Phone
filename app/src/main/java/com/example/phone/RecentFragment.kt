package com.example.phone

import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phone.Adapter.CallLogAdapter
import com.example.phone.Model.CallLogItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class RecentFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var callLogAdapter: CallLogAdapter
    private val callLogList = mutableListOf<CallLogItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recent, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        callLogAdapter = CallLogAdapter(callLogList, requireContext())
        recyclerView.adapter = callLogAdapter

        fetchCallLogs()

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchCallLogs()
    }

    private fun fetchCallLogs() {
        callLogList.clear()
        val contentResolver = context?.contentResolver

        val projection = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )

        val cursor = contentResolver?.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            val currentTime = System.currentTimeMillis()
            val today = Calendar.getInstance().apply { timeInMillis = currentTime }
            val yesterday = Calendar.getInstance().apply { timeInMillis = currentTime }
            yesterday.add(Calendar.DAY_OF_YEAR, -1)

            while (it.moveToNext()) {
                val number = it.getString(numberIndex) ?: "Unknown"
                val type = it.getInt(typeIndex)
                val dateMillis = it.getLong(dateIndex)
                val duration = it.getInt(durationIndex)

                val callDate = Calendar.getInstance().apply { timeInMillis = dateMillis }

                if (isSameDay(callDate, today) || isSameDay(callDate, yesterday)) {
                    val formattedDate = when {
                        isSameDay(callDate, today) -> "Today"
                        isSameDay(callDate, yesterday) -> "Yesterday"
                        else -> SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(dateMillis))
                    }

                    val typeString = when (type) {
                        CallLog.Calls.INCOMING_TYPE -> "Incoming"
                        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                        CallLog.Calls.MISSED_TYPE -> "Missed"
                        CallLog.Calls.REJECTED_TYPE -> "Rejected"
                        else -> "Unknown"
                    }

                    val contactName = getContactName(number) ?: number

                    callLogList.add(CallLogItem(number, contactName, typeString, formattedDate, "$duration sec"))
                }
            }
        }

        cursor?.close()
        callLogAdapter.notifyDataSetChanged()
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun getContactName(phoneNumber: String): String? {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val cursor = requireContext().contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return null
    }
}