package com.example.telephonebookusingrecyleview

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.telephonebookusingrecyleview.databinding.ItemContactBinding
import kotlin.math.abs

class ContactAdapter(
    private val onItemClick: (Contact) -> Unit,
    private val onDeleteClick: (Contact) -> Unit,
) : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {

    private val avatarColors = intArrayOf(
        "#E57373".toColorInt(),
        "#F06292".toColorInt(),
        "#BA68C8".toColorInt(),
        "#9575CD".toColorInt(),
        "#7986CB".toColorInt(),
        "#64B5F6".toColorInt(),
        "#4FC3F7".toColorInt(),
        "#4DD0E1".toColorInt(),
        "#4DB6AC".toColorInt(),
        "#81C784".toColorInt(),
        "#AED581".toColorInt(),
        "#FFB74D".toColorInt(),
        "#FF8A65".toColorInt(),
        "#A1887F".toColorInt(),
        "#90A4AE".toColorInt(),
    )

    class ContactViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.binding.tvName.text = contact.name
        holder.binding.tvPhone.text = contact.phone
        holder.binding.tvEmail.text = contact.email

        val initial = contact.name.trim().take(1).uppercase()
        holder.binding.tvAvatar.text = initial.ifEmpty { "?" }

        val colorIndex = abs(contact.name.hashCode()) % avatarColors.size
        holder.binding.tvAvatar.backgroundTintList =
            ColorStateList.valueOf(avatarColors[colorIndex])

        holder.binding.root.setOnClickListener {
            onItemClick(contact)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(contact)
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}
