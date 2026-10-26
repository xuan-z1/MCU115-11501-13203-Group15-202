package com.example.telephonebookusingrecyleview

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telephonebookusingrecyleview.databinding.FragmentFirstBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupAddButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(
            onItemClick = { contact ->
                showContactDetailDialog(contact)
            },
            onDeleteClick = { contact ->
                showDeleteConfirmationDialog(contact)
            },
        )

        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }
    }

    private fun showDeleteConfirmationDialog(contact: Contact) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_dialog_title)
            .setMessage(getString(R.string.delete_dialog_msg, contact.name))
            .setPositiveButton(R.string.confirm) { _, _ ->
                viewModel.deleteContact(contact)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_contact_deleted, contact.name),
                    Toast.LENGTH_SHORT,
                ).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showContactDetailDialog(contact: Contact) {
        val options = arrayOf(
            getString(R.string.action_call) + " (${contact.phone})",
            getString(R.string.action_email) + " (${contact.email})",
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(contact.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:${contact.phone}".toUri()
                        }
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:${contact.email}".toUri()
                        }
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString().orEmpty()
                viewModel.setSearchQuery(query)
                binding.btnClearSearch.isVisible = query.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }
    }

    private fun setupAddButton() {
        binding.btnAddContact.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.filteredContacts.observe(viewLifecycleOwner) { contacts ->
            contactAdapter.submitList(contacts)

            binding.tvEmptyState.isVisible = contacts.isEmpty()

            val currentQuery = viewModel.searchQuery.value.orEmpty()
            binding.tvContactCount.text = if (currentQuery.isBlank()) {
                getString(R.string.contact_count_total, contacts.size)
            } else {
                getString(R.string.contact_count_filtered, contacts.size)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
