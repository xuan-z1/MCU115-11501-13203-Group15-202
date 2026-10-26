package com.example.pageswitching

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pageswitching.databinding.ActivitySecBinding

class SecActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySecBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
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
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        currentFocus?.let { view ->
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    companion object {
        const val EXTRA_INPUT_TEXT = "input_key"
        const val EXTRA_REPLY_TEXT = "reply_key"
    }
}
