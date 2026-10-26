package com.example.fastfoodorderingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fastfoodorderingapp.databinding.FragmentDrinkBinding

class DrinkFragment : Fragment() {

    private var _binding: FragmentDrinkBinding? = null
    private val binding get() = _binding!!

    private val vm: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentOrder = vm.order.value
        when (currentOrder?.drink) {
            "Cola" -> binding.rbCola.isChecked = true
            "Iced Tea" -> binding.rbIcedTea.isChecked = true
            "Water" -> binding.rbWater.isChecked = true
        }

        binding.btnDone.setOnClickListener {
            val selectedId = binding.rgDrink.checkedRadioButtonId
            val selectedDrink = if (selectedId != -1) {
                binding.root.findViewById<RadioButton>(selectedId)?.text?.toString()
            } else {
                null
            }
            vm.updateDrink(selectedDrink)
            (activity as? MainActivity)?.hideFragmentContainer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
