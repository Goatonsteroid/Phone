package com.example.phone

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phone.Adapter.MyAdapter
import com.example.phone.Database.DatabaseHelper
import com.example.phone.Model.Contact

class ContactFragment : Fragment() {

    private lateinit var contactAdapter: MyAdapter
    private lateinit var contactList: List<Contact>
    private lateinit var searchView: androidx.appcompat.widget.SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { fetchContactsAndSaveToDB(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        searchView = view.findViewById(R.id.search_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val databaseHelper = DatabaseHelper(requireContext())

        // Fetch contacts from database
        contactList = databaseHelper.getAllContacts()
        contactAdapter = MyAdapter(contactList)

        recyclerView.adapter = contactAdapter

        setupSearchView()
        searchView.isFocusable = true
        searchView.isIconified = false
        searchView.clearFocus() // To prevent auto-focus when fragment loads


        return view
    }

    private fun fetchContactsAndSaveToDB(context: Context) {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )

        val contactDatabaseHelper = DatabaseHelper(context)

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(0)
                val phone = it.getString(1)
                contactDatabaseHelper.addContact(name, phone)
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { contactAdapter.filter(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { contactAdapter.filter(it) }
                return true
            }
        })
    }
}