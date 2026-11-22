package com.example.myapplication

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class TimerService : Service() {

    companion object {
        const val ACTION_TIMER_UPDATE = "com.example.myapplication.TIMER_UPDATE"
        const val EXTRA_DURATION_SEC = "duration_sec"
        const val EXTRA_REMAINING_SEC = "remaining_sec"
        const val EXTRA_TOTAL_SEC = "total_sec"

        private const val CHANNEL_ID = "pomodoro_timer_channel"
        private const val NOTIFICATION_ID = 1001
    }

    @Volatile
    private var remainingSec = 0

    @Volatile
    private var totalSec = 0

    @Volatile
    private var running = false
    private var worker: Thread? = null

    private val systemReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> {
                    val min = (remainingSec + 59) / 60
                    Toast.makeText(
                        context,
                        "Focus Reminder: $min mins remaining!",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                Intent.ACTION_HEADSET_PLUG -> {
                    val state = intent.getIntExtra("state", -1)
                    if (state == 0) {
                        Toast.makeText(context, "Headset unplugged: Focus sound paused", Toast.LENGTH_SHORT).show()
                    } else if (state == 1) {
                        Toast.makeText(context, "Headset plugged: Focus sound resumed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (running) return START_NOT_STICKY

        totalSec = intent?.getIntExtra(EXTRA_DURATION_SEC, 25 * 60) ?: (25 * 60)
        remainingSec = totalSec
        running = true

        createNotificationChannel()
        updateNotification(remainingSec)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_HEADSET_PLUG)
        }
        ContextCompat.registerReceiver(
            this,
            systemReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        worker = Thread {
            try {
                sendUpdateBroadcast(remainingSec, totalSec)

                while (running && (remainingSec > 0)) {
                    Thread.sleep(1000)
                    if (!running) break
                    remainingSec--
                    updateNotification(remainingSec)
                    sendUpdateBroadcast(remainingSec, totalSec)
                }

                if (running && (remainingSec == 0)) {
                    val finishIntent = Intent(this, FinishActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(finishIntent)
                }
            } catch (_: InterruptedException) {
            } finally {
                stopSelf()
            }
        }.also { it.start() }

        return START_NOT_STICKY
    }

    private fun sendUpdateBroadcast(remaining: Int, total: Int) {
        val updateIntent = Intent(ACTION_TIMER_UPDATE).apply {
            setPackage(packageName)
            putExtra(EXTRA_REMAINING_SEC, remaining)
            putExtra(EXTRA_TOTAL_SEC, total)
        }
        sendBroadcast(updateIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Shows ongoing Pomodoro timer notification"
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(sec: Int): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", sec / 60, sec % 60)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText("Remaining Time: $timeStr")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun updateNotification(sec: Int) {
        try {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, createNotification(sec))
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        running = false
        worker?.interrupt()
        try {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(NOTIFICATION_ID)
        } catch (_: Exception) {
        }
        try {
            unregisterReceiver(systemReceiver)
        } catch (_: IllegalArgumentException) {
        }
        super.onDestroy()
    }
}