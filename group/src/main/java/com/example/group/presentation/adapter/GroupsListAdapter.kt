package com.example.group.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.group.R
import com.example.group.databinding.ItemGroupBinding
import com.example.group.domain.model.GroupDomain

class GroupsListAdapter(private val listener: Listener) :
    ListAdapter<GroupDomain, GroupsListAdapter.GroupViewHolder>(Callback) {

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.title.text = groupName
                binding.participantsCount.text = binding.root.resources.getQuantityString(R.plurals.students_plurals, studentsCount, studentsCount)
                binding.root.setOnClickListener {
                    listener.onGroupClick(this)
                }
            }
        }
    }

    object Callback : DiffUtil.ItemCallback<GroupDomain>() {
        override fun areItemsTheSame(oldItem: GroupDomain, newItem: GroupDomain): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: GroupDomain, newItem: GroupDomain): Boolean {
            return oldItem == newItem
        }

    }

    override fun getItemCount(): Int = currentList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            ItemGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(position)
    }

    interface Listener {
        fun onGroupClick(item: GroupDomain)
    }
}