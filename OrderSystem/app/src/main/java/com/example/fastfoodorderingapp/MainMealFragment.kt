package com.example.fastfoodorderingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fastfoodorderingapp.databinding.FragmentMainMealBinding

class MainMealFragment : Fragment() {

    private var _binding: FragmentMainMealBinding? = null
    private val binding get() = _binding!!

    private val vm: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentOrder = vm.order.value
        when (currentOrder?.mainMeal) {
            "Burger" -> binding.rbBurger.isChecked = true
            "Fried Chicken" -> binding.rbFriedChicken.isChecked = true
            "Chicken Nuggets" -> binding.rbChickenNuggets.isChecked = true
        }

        binding.btnDone.setOnClickListener {
            val selectedId = binding.rgMainMeal.checkedRadioButtonId
            val selectedMainMeal = if (selectedId != -1) {
                binding.root.findViewById<RadioButton>(selectedId)?.text?.toString()
            } else {
                null
            }
            vm.updateMainMeal(selectedMainMeal)
            (activity as? MainActivity)?.hideFragmentContainer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
