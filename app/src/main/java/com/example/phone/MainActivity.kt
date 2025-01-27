package com.example.phone

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    // Declaration of views
    private lateinit var frame: FrameLayout
    private lateinit var navBar: BottomNavigationView
    private lateinit var keypad: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialisation
        frame = findViewById(R.id.frameLayout)
        navBar = findViewById(R.id.bottomNavigation)
        keypad = findViewById(R.id.keypad)

        // Changing the fragments
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout,RecentFragment())
                .commit()
        }

        navBar.setOnItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.recent -> {
                    replaceFragment(RecentFragment())
                    true
                }
                R.id.contacts -> {
                    replaceFragment(ContactFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

}