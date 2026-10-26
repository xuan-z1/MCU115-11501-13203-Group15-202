package com.example.fastfoodorderingapp

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.fastfoodorderingapp.databinding.ActivityDrinkBinding

open class DrinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrinkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentDrink = intent.getStringExtra(EXTRA_DRINK)
        when (currentDrink) {
            "Cola" -> binding.rbCola.isChecked = true
            "Iced Tea" -> binding.rbIcedTea.isChecked = true
            "Water" -> binding.rbWater.isChecked = true
        }

        binding.btnDone.setOnClickListener {
            val selectedId = binding.rgDrink.checkedRadioButtonId
            val selectedDrink = if (selectedId != -1) {
                findViewById<RadioButton>(selectedId)?.text?.toString()
            } else {
                null
            }

            val resultIntent = Intent().apply {
                putExtra(EXTRA_DRINK, selectedDrink)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_DRINK = "extra_drink"
    }
}
