package com.example.fastfoodorderingapp

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fastfoodorderingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm.order.observe(this) { order ->
            updateUI(order)
        }

        binding.btnChooseMainMeal.setOnClickListener {
            showFragment(MainMealFragment())
        }

        binding.btnChooseSideDishes.setOnClickListener {
            showFragment(SideDishesFragment())
        }

        binding.btnChooseDrink.setOnClickListener {
            showFragment(DrinkFragment())
        }

        binding.btnOrder.setOnClickListener {
            showFragment(ConfirmFragment())
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.fragmentContainer.visibility == View.VISIBLE) {
                    hideFragmentContainer()
                } else {
                    finish()
                }
            }
        })
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        binding.fragmentContainer.visibility = View.VISIBLE
    }

    fun hideFragmentContainer() {
        binding.fragmentContainer.visibility = View.GONE
        supportFragmentManager.findFragmentById(R.id.fragmentContainer)?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    private fun updateUI(order: Order?) {
        val mainText = order?.mainMeal ?: "—"
        val sidesText = if (order?.sideDishes?.isNotEmpty() == true) order.sideDishes.joinToString(", ") else "—"
        val drinkText = order?.drink ?: "—"

        binding.tvCurrentSelection.text = "Main: $mainText\n\nSides: $sidesText\n\nDrink: $drinkText"
    }
}
