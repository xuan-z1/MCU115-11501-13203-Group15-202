package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted, reminding user to start daily study session")
            Toast.makeText(
                context,
                context.getString(R.string.boot_completed_msg),
                Toast.LENGTH_LONG,
            ).show()
        }
    }
}