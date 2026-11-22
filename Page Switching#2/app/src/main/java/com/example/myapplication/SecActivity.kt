package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivitySecBinding

class SecActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySecBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.secMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val receivedText = intent.getStringExtra(EXTRA_INPUT_TEXT)
        if (!receivedText.isNullOrEmpty()) {
            binding.tvReceived.text = receivedText
        }

        binding.btnReturn.setOnClickListener {
            hideKeyboard()
            val replyText = binding.etReply.text?.toString().orEmpty()
            val resultIntent = Intent().apply {
                putExtra(EXTRA_REPLY_TEXT, replyText)
            }
            setResult(RESULT_OK, resultIntent)
            finish()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            } else {
                @Suppress("DEPRECATION")
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        currentFocus?.let {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    companion object {
        const val EXTRA_INPUT_TEXT = "input_key"
        const val EXTRA_REPLY_TEXT = "reply_key"
    }
}