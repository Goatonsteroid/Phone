package com.example.phone

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService

class MyInCallService: InCallService() {
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        call?.registerCallback(callCallback)
        val intent = Intent(this@MyInCallService,IncomingCall::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("CALL_ID", call?.details?.handle?.schemeSpecificPart)
        }
        startActivity(intent)
    }

    private val callCallback = object: Call.Callback(){
        override fun onStateChanged(call: Call?, state: Int) {
            super.onStateChanged(call, state)
            when(state){
                Call.STATE_DISCONNECTED -> {
                    val intent = Intent("CALL_DISCONNECTED")
                    sendBroadcast(intent)
                }

                Call.STATE_ACTIVE -> {
                    TODO()
                }

                Call.STATE_AUDIO_PROCESSING -> {
                    TODO()
                }

                Call.STATE_CONNECTING -> {
                    TODO()
                }

                Call.STATE_DIALING -> {
                    TODO()
                }

                Call.STATE_DISCONNECTING -> {
                    TODO()
                }

                Call.STATE_HOLDING -> {
                    TODO()
                }

                Call.STATE_NEW -> {
                    TODO()
                }

                Call.STATE_PULLING_CALL -> {
                    TODO()
                }

                Call.STATE_RINGING -> {
                    TODO()
                }

                Call.STATE_SELECT_PHONE_ACCOUNT -> {
                    TODO()
                }

                Call.STATE_SIMULATED_RINGING -> {
                    TODO()
                }
            }
        }
    }

}