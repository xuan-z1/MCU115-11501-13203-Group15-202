package com.example.telephonebookusingrecyleview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.telephonebookusingrecyleview.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirmAdd.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            binding.tilName.error = null
            binding.tilPhone.error = null
            binding.tilEmail.error = null

            var isValid = true

            if (name.isEmpty()) {
                binding.tilName.error = getString(R.string.hint_name)
                isValid = false
            }

            if (phone.isEmpty()) {
                binding.tilPhone.error = getString(R.string.hint_phone)
                isValid = false
            }

            if (email.isEmpty()) {
                binding.tilEmail.error = getString(R.string.hint_email)
                isValid = false
            }

            if (isValid) {
                val newContact = Contact(
                    name = name,
                    phone = phone,
                    email = email,
                )
                viewModel.addContact(newContact)

                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_contact_added),
                    Toast.LENGTH_SHORT,
                ).show()

                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_empty_fields),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
