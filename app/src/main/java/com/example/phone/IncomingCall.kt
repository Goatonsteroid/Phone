package com.example.phone

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton


class IncomingCall : AppCompatActivity() {
    private lateinit var callerNameText: TextView
    private lateinit var callerNumberText: TextView
    private lateinit var acceptButton: MaterialButton
    private lateinit var rejectButton: MaterialButton
    private lateinit var telecomManager: TelecomManager
    private var contactName: String? = null
    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        arrayOf(
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_PHONE_STATE
        )
    } else {
        arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_PHONE_STATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)

        // Keep screen on during incoming call
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Show on lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        if (!checkPermissions()) {
            requestPermissions()
        }
        initializeViews()
    }

    private fun checkPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            1
        )
    }

    private fun initializeViews() {
        callerNameText = findViewById(R.id.callerName)
        callerNumberText = findViewById(R.id.callerNumber)
        acceptButton = findViewById(R.id.acceptButton)
        rejectButton = findViewById(R.id.rejectButton)

        telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager

        // Set up button click listeners
        acceptButton.setOnClickListener { }
        rejectButton.setOnClickListener {  }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}