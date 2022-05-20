package com.edu.chat.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.chat.R
import com.edu.chat.databinding.ItemChatBinding
import com.edu.chat.databinding.ItemHeaderBinding
import com.edu.chat.domain.model.ChatMember
import com.edu.common.utils.imageLoading.IImageLoader

class ChatNonMembersAdapter(
    private val listener: ItemClickListener,
    private val imageLoader: IImageLoader
) : ListAdapter<ChatMember, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<ChatMember>() {
    override fun areItemsTheSame(oldItem: ChatMember, newItem: ChatMember): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: ChatMember, newItem: ChatMember): Boolean {
        return oldItem == newItem
    }

}) {

    companion object {
        private const val FIRST_POSITION = 0
        private const val SECOND_POSITION = 1
    }

    inner class NonMemberVH(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.lastMessageTime.isVisible = false
                binding.lastMessage.isVisible = false
                binding.studentName.text = name
                imageLoader.loadImageWithCircleShape(
                    avatarUrl,
                    binding.logo,
                    R.drawable.ic_chat_item
                )
                binding.root.setOnClickListener {
                    listener.onClick(this)
                }
            }
        }
    }

    class HeaderVH(val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == FIRST_POSITION) {
            HeaderVH(ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            NonMemberVH(
                ItemChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > FIRST_POSITION && holder is NonMemberVH) {
            holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == FIRST_POSITION) FIRST_POSITION else SECOND_POSITION
    }


    interface ItemClickListener {
        fun onClick(member: ChatMember)
    }

}