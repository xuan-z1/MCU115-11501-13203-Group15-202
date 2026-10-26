package com.example.fastfoodorderingapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fastfoodorderingapp.databinding.ActivitySideDishesBinding

open class SideDishesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySideDishesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySideDishesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentSides = intent.getStringArrayListExtra(EXTRA_SIDE_DISHES) ?: arrayListOf()
        binding.cbFries.isChecked = currentSides.contains("Fries")
        binding.cbSalad.isChecked = currentSides.contains("Salad")
        binding.cbCornCup.isChecked = currentSides.contains("Corn Cup")

        binding.btnDone.setOnClickListener {
            val selectedSides = ArrayList<String>()
            if (binding.cbFries.isChecked) selectedSides.add("Fries")
            if (binding.cbSalad.isChecked) selectedSides.add("Salad")
            if (binding.cbCornCup.isChecked) selectedSides.add("Corn Cup")

            val resultIntent = Intent().apply {
                putStringArrayListExtra(EXTRA_SIDE_DISHES, selectedSides)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_SIDE_DISHES = "extra_side_dishes"
    }
}
