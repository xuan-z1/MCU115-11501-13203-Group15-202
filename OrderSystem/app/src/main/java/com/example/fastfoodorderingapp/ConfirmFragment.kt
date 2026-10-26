package com.example.fastfoodorderingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fastfoodorderingapp.databinding.FragmentConfirmBinding

class ConfirmFragment : Fragment() {

    private var _binding: FragmentConfirmBinding? = null
    private val binding get() = _binding!!

    private val vm: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.order.observe(viewLifecycleOwner) { order ->
            updateSummaryText(order)
        }

        binding.btnConfirm.setOnClickListener {
            val order = vm.order.value
            if (order?.isComplete() != true) {
                Toast.makeText(
                    requireContext(),
                    "Please select a main meal, at least one side dish, and a drink.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val sidesString = order.sideDishes.joinToString(", ")
            AlertDialog.Builder(requireContext())
                .setTitle("Submit Order")
                .setMessage(
                    "Main: ${order.mainMeal}\n" +
                            "Sides: $sidesString\n" +
                            "Drink: ${order.drink}\n\nSubmit this order?"
                )
                .setPositiveButton("Submit") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Order submitted!", Toast.LENGTH_SHORT).show()
                    (activity as? MainActivity)?.hideFragmentContainer()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun updateSummaryText(order: Order?) {
        if (order == null || (!order.isComplete() && order.mainMeal == null && order.sideDishes.isEmpty() && order.drink == null)) {
            binding.tvOrderSummary.text = "Your order summary will appear here."
            return
        }

        val mainText = order.mainMeal ?: "—"
        val sidesText = if (order.sideDishes.isNotEmpty()) order.sideDishes.joinToString(", ") else "—"
        val drinkText = order.drink ?: "—"

        binding.tvOrderSummary.text = "Main: $mainText\n\nSides: $sidesText\n\nDrink: $drinkText"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
