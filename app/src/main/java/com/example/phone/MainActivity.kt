package com.example.phone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    // Declaration of views
    private lateinit var frame: FrameLayout
    private lateinit var navBar: BottomNavigationView
    private lateinit var keypad: FloatingActionButton
    private lateinit var keypadContainer: LinearLayout
    private lateinit var numberScreen: TextInputEditText
    // Buttons
    private lateinit var callButton: MaterialButton
    private lateinit var button0: MaterialButton
    private lateinit var button1: MaterialButton
    private lateinit var button2: MaterialButton
    private lateinit var button3: MaterialButton
    private lateinit var button4: MaterialButton
    private lateinit var button5: MaterialButton
    private lateinit var button6: MaterialButton
    private lateinit var button7: MaterialButton
    private lateinit var button8: MaterialButton
    private lateinit var button9: MaterialButton
    private lateinit var buttonStar: MaterialButton
    private lateinit var buttonHash: MaterialButton

    private val PERMISSION_CODE = 100;

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
        keypadContainer = findViewById(R.id.keypadContainer)
        numberScreen = findViewById(R.id.numberScreen)
        //Buttons
        callButton = findViewById(R.id.callButton)
        button0 = findViewById(R.id.button0)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button7 = findViewById(R.id.button7)
        button8 = findViewById(R.id.button8)
        button9 = findViewById(R.id.button9)
        buttonStar = findViewById(R.id.buttonStar)
        buttonHash = findViewById(R.id.buttonHash)

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

        // fab listener
        keypad.setOnClickListener {
            if (keypadContainer.visibility == LinearLayout.GONE) {
                showKeypadContainer(keypadContainer)
            }
        }

        // to prevent keyboard from showing
        numberScreen.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.clearFocus()
            }
        }
        // Appending numbers to EditText
        button1.setOnClickListener { appendNumber("1") }
        button2.setOnClickListener { appendNumber("2") }
        button3.setOnClickListener { appendNumber("3") }
        button4.setOnClickListener { appendNumber("4") }
        button5.setOnClickListener { appendNumber("5") }
        button6.setOnClickListener { appendNumber("6") }
        button7.setOnClickListener { appendNumber("7") }
        button8.setOnClickListener { appendNumber("8") }
        button9.setOnClickListener { appendNumber("9") }
        button0.setOnClickListener { appendNumber("0") }
        buttonStar.setOnClickListener{appendNumber("*")}
        buttonHash.setOnClickListener{appendNumber("#")}

        // Checking permission for call feature and Calling number
        val permission = Manifest.permission.CALL_PHONE
        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_CODE)
        }
            callButton.setOnClickListener {
            val phoneNumber = numberScreen.text.toString()
            if (phoneNumber.isNotEmpty()) {
                callNumber(phoneNumber)
            }
        }

    }

    private fun callNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    private fun appendNumber(s: String) {
        numberScreen.append(s)
    }

    // Animation of the keypad Container
    private fun showKeypadContainer(keypadContainer: LinearLayout) {
        keypadContainer.visibility = LinearLayout.VISIBLE
        val slideUp = TranslateAnimation(0f, 0f, keypadContainer.height.toFloat(), 0f)
        slideUp.duration = 300
        slideUp.fillAfter = true
        keypadContainer.startAnimation(slideUp)
    }

    // Replacing the fragment
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

}