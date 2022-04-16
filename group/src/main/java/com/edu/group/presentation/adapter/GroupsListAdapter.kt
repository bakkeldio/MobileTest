package com.edu.group.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.group.R
import com.edu.group.databinding.ItemGroupBinding
import com.edu.group.domain.model.GroupDomain

class GroupsListAdapter(private val listener: Listener, private val imageLoader: IImageLoader) :
    ListAdapter<GroupDomain, GroupsListAdapter.GroupViewHolder>(Callback) {

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                imageLoader.loadImageWithCircleShape(avatar, binding.groupLogo)
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