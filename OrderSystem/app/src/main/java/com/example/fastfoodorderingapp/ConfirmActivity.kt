package com.example.fastfoodorderingapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fastfoodorderingapp.databinding.ActivityConfirmBinding

open class ConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainMeal = intent.getStringExtra(EXTRA_MAIN_MEAL)
        val sideDishes = intent.getStringArrayListExtra(EXTRA_SIDE_DISHES) ?: arrayListOf()
        val drink = intent.getStringExtra(EXTRA_DRINK)

        updateSummaryText(mainMeal, sideDishes, drink)

        binding.btnConfirm.setOnClickListener {
            val isComplete = !mainMeal.isNullOrEmpty() && sideDishes.isNotEmpty() && !drink.isNullOrEmpty()

            if (!isComplete) {
                Toast.makeText(
                    this,
                    "Please select a main meal, at least one side dish, and a drink.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val sidesString = sideDishes.joinToString(", ")
            AlertDialog.Builder(this)
                .setTitle("Submit Order")
                .setMessage(
                    "Main: $mainMeal\n" +
                            "Sides: $sidesString\n" +
                            "Drink: $drink\n\nSubmit this order?"
                )
                .setPositiveButton("Submit") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(this, "Order submitted!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun updateSummaryText(mainMeal: String?, sideDishes: List<String>, drink: String?) {
        val hasSelection = !mainMeal.isNullOrEmpty() || sideDishes.isNotEmpty() || !drink.isNullOrEmpty()

        if (!hasSelection) {
            binding.tvOrderSummary.text = "Your order summary will appear here."
            return
        }

        val mainText = mainMeal ?: "—"
        val sidesText = if (sideDishes.isNotEmpty()) sideDishes.joinToString(", ") else "—"
        val drinkText = drink ?: "—"

        binding.tvOrderSummary.text = "Main: $mainText\n\nSides: $sidesText\n\nDrink: $drinkText"
    }

    companion object {
        const val EXTRA_MAIN_MEAL = "extra_main_meal"
        const val EXTRA_SIDE_DISHES = "extra_side_dishes"
        const val EXTRA_DRINK = "extra_drink"
    }
}
