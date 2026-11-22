package com.example.myapplication

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sec = intent.getIntExtra(TimerService.EXTRA_REMAINING_SEC, 0)
            val total = intent.getIntExtra(TimerService.EXTRA_TOTAL_SEC, 25 * 60)

            binding.tvTimer.text = String.format(Locale.getDefault(), "%02d:%02d", sec / 60, sec % 60)

            if (total > 0) {
                val progress = ((total - sec) * 100) / total
                binding.progressTimer.progress = progress
            }

            if (sec == 0) {
                resetUiState(context)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        ContextCompat.registerReceiver(
            this,
            timerReceiver,
            IntentFilter(TimerService.ACTION_TIMER_UPDATE),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        binding.btnStart.setOnClickListener {
            val selectedDuration = getSelectedDurationSec()
            val intent = Intent(this, TimerService::class.java).apply {
                putExtra(TimerService.EXTRA_DURATION_SEC, selectedDuration)
            }

            startService(intent)

            binding.tvStatus.text = getString(R.string.focusing)
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.primary_red))
            binding.btnStart.isEnabled = false
            binding.btnStop.isEnabled = true
            setChipGroupEnabled(false)
        }

        binding.btnStop.setOnClickListener {
            stopService(Intent(this, TimerService::class.java))
            resetUiState(this)
        }
    }

    private fun getSelectedDurationSec(): Int {
        return when (binding.chipGroupDuration.checkedChipId) {
            R.id.chip5m -> 5 * 60
            R.id.chip10s -> 10
            else -> 25 * 60
        }
    }

    private fun setChipGroupEnabled(enabled: Boolean) {
        binding.chip25m.isEnabled = enabled
        binding.chip5m.isEnabled = enabled
        binding.chip10s.isEnabled = enabled
    }

    private fun resetUiState(context: Context) {
        binding.tvStatus.text = getString(R.string.ready_to_focus)
        binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text))
        binding.tvTimer.text = getString(R.string.initial_timer)
        binding.progressTimer.progress = 0
        binding.btnStart.isEnabled = true
        binding.btnStop.isEnabled = false
        setChipGroupEnabled(true)
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(timerReceiver)
        } catch (_: IllegalArgumentException) {
        }
        super.onDestroy()
    }
}