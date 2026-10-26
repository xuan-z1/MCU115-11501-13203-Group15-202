package com.example.fastfoodorderingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fastfoodorderingapp.databinding.FragmentSideDishesBinding

class SideDishesFragment : Fragment() {

    private var _binding: FragmentSideDishesBinding? = null
    private val binding get() = _binding!!

    private val vm: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSideDishesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentSides = vm.order.value?.sideDishes ?: emptyList()
        binding.cbFries.isChecked = currentSides.contains("Fries")
        binding.cbSalad.isChecked = currentSides.contains("Salad")
        binding.cbCornCup.isChecked = currentSides.contains("Corn Cup")

        binding.btnDone.setOnClickListener {
            val selectedSides = mutableListOf<String>()
            if (binding.cbFries.isChecked) selectedSides.add("Fries")
            if (binding.cbSalad.isChecked) selectedSides.add("Salad")
            if (binding.cbCornCup.isChecked) selectedSides.add("Corn Cup")

            vm.updateSideDishes(selectedSides)
            (activity as? MainActivity)?.hideFragmentContainer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
