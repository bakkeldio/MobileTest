package com.edu.mobiletestadmin.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.databinding.ItemGroupBinding
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.utils.IImageLoader

class GroupsAdapter(private val listener: Listener, private val imageLoader: IImageLoader) :
    ListAdapter<UserGroup, GroupsAdapter.GroupVH>(object : DiffUtil.ItemCallback<UserGroup>() {
        override fun areItemsTheSame(oldItem: UserGroup, newItem: UserGroup): Boolean {
            return oldItem.groupUid == newItem.groupUid
        }

        override fun areContentsTheSame(oldItem: UserGroup, newItem: UserGroup): Boolean {
            return oldItem == newItem
        }

    }) {

    inner class GroupVH(val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.divider.visibility = if (position != currentList.lastIndex) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
                binding.groupNameTV.text = groupName
                imageLoader.loadImageWithCircleShape(
                    binding.groupLogo,
                    groupAvatar,
                    R.drawable.ic_fa6_solid_user_group
                )
                binding.root.setOnClickListener {
                    listener.onGroupClick(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupVH {
        return GroupVH(ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: GroupVH, position: Int) {
        holder.bind(position)
    }

    interface Listener {
        fun onGroupClick(group: UserGroup)
    }
}