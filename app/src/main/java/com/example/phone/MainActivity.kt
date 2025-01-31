package com.example.phone

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    // Views
    private lateinit var frame: FrameLayout
    private lateinit var navBar: BottomNavigationView
    private lateinit var keypad: FloatingActionButton
    private lateinit var keypadContainer: LinearLayout
    private lateinit var numberScreen: TextInputEditText
    private lateinit var callButton: MaterialButton
    private lateinit var outerLayer: TextInputLayout

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        frame = findViewById(R.id.frameLayout)
        navBar = findViewById(R.id.bottomNavigation)
        keypad = findViewById(R.id.keypad)
        keypadContainer = findViewById(R.id.keypadContainer)
        numberScreen = findViewById(R.id.numberScreen)
        callButton = findViewById(R.id.callButton)
        outerLayer = findViewById(R.id.outer_layer)

        // Initial fragment transaction
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, RecentFragment())
                .commit()
        }

        // Handle bottom navigation
        navBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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

        // Handle fab visibility
        keypad.setOnClickListener {
            if (keypadContainer.visibility == LinearLayout.GONE) {
                showKeypadContainer(keypadContainer)
            }
        }

        // Prevent keyboard from showing
        numberScreen.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.clearFocus()
            }
        }

        // Append numbers to the EditText
        findViewById<MaterialButton>(R.id.button0).setOnClickListener { appendNumber("0") }
        findViewById<MaterialButton>(R.id.button1).setOnClickListener { appendNumber("1") }
        findViewById<MaterialButton>(R.id.button2).setOnClickListener { appendNumber("2") }
        findViewById<MaterialButton>(R.id.button3).setOnClickListener { appendNumber("3") }
        findViewById<MaterialButton>(R.id.button4).setOnClickListener { appendNumber("4") }
        findViewById<MaterialButton>(R.id.button5).setOnClickListener { appendNumber("5") }
        findViewById<MaterialButton>(R.id.button6).setOnClickListener { appendNumber("6") }
        findViewById<MaterialButton>(R.id.button7).setOnClickListener { appendNumber("7") }
        findViewById<MaterialButton>(R.id.button8).setOnClickListener { appendNumber("8") }
        findViewById<MaterialButton>(R.id.button9).setOnClickListener { appendNumber("9") }
        findViewById<MaterialButton>(R.id.buttonStar).setOnClickListener { appendNumber("*") }
        findViewById<MaterialButton>(R.id.buttonHash).setOnClickListener { appendNumber("#") }

        launchSetDefaultDialerIntent(this)

        // Check and request permissions
        checkPermissions()

        // Set the call button action
        callButton.setOnClickListener {
            val phoneNumber = numberScreen.text.toString()
            if (phoneNumber.isNotEmpty()) {
                callNumber(phoneNumber)
            }
        }
        outerLayer.setEndIconOnClickListener{
            hideKeypadContainer(keypadContainer)
        }
    }

    override fun onResume() {
        super.onResume()
        keypad.setOnClickListener {
            if (keypadContainer.visibility == LinearLayout.GONE) {
                showKeypadContainer(keypadContainer)
            }
        }
    }

    private fun hideKeypadContainer(keypadContainer: LinearLayout) {
        val slideDown = TranslateAnimation(0f, 0f, 0f, keypadContainer.height.toFloat())
        slideDown.duration = 300
        slideDown.fillAfter = true
        keypadContainer.startAnimation(slideDown)
    }

    // Make an outgoing call
    private fun callNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    // Append numbers to the number screen
    private fun appendNumber(s: String) {
        numberScreen.append(s)
    }

    // Show the keypad container with animation
    private fun showKeypadContainer(keypadContainer: LinearLayout) {
            keypadContainer.visibility = LinearLayout.VISIBLE
            val slideUp = TranslateAnimation(0f, 0f, keypadContainer.height.toFloat(), 0f)
            slideUp.duration = 300
            slideUp.fillAfter = true
            keypadContainer.startAnimation(slideUp)
    }

    // Replace fragments in the main view
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    // Check and request runtime permissions
    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.MANAGE_OWN_CALLS,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS
        )
        permissionRequestLauncher.launch(permissions)
    }

    // Check if the app is the default dialer
    @SuppressLint("QueryPermissionsNeeded")
    fun launchSetDefaultDialerIntent(activity: AppCompatActivity) {
        Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
            TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
            activity.packageName
        ).apply {
            if (resolveActivity(activity.packageManager) != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val rm: RoleManager? = activity.getSystemService(RoleManager::class.java)
                    if (rm?.isRoleAvailable(RoleManager.ROLE_DIALER) == true) {
                        @Suppress("DEPRECATION")
                        activity.startActivityForResult(
                            rm.createRequestRoleIntent(RoleManager.ROLE_DIALER),
                            1
                        )
                    }
                } else {
                    @Suppress("DEPRECATION")
                    activity.startActivityForResult(this, 1)
                }
            }
        }
    }

    // Handling user response
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(this,"It is default app",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"It is not default app",Toast.LENGTH_SHORT).show()
            }
        }
    }
}