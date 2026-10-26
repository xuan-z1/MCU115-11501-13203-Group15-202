package com.example.fastfoodorderingapp

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.fastfoodorderingapp.databinding.ActivityMainMealBinding

open class MainMealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMealBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentMainMeal = intent.getStringExtra(EXTRA_MAIN_MEAL)
        when (currentMainMeal) {
            "Burger" -> binding.rbBurger.isChecked = true
            "Fried Chicken" -> binding.rbFriedChicken.isChecked = true
            "Chicken Nuggets" -> binding.rbChickenNuggets.isChecked = true
        }

        binding.btnDone.setOnClickListener {
            val selectedId = binding.rgMainMeal.checkedRadioButtonId
            val selectedMainMeal = if (selectedId != -1) {
                findViewById<RadioButton>(selectedId)?.text?.toString()
            } else {
                null
            }

            val resultIntent = Intent().apply {
                putExtra(EXTRA_MAIN_MEAL, selectedMainMeal)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_MAIN_MEAL = "extra_main_meal"
    }
}
