package com.edu.chat.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.chat.R
import com.edu.chat.databinding.ItemChatBinding
import com.edu.chat.domain.model.ChatMemberItem
import com.edu.chat.domain.model.MessageAuthorEnum
import com.edu.chat.domain.model.MessageStatusEnum
import com.edu.chat.domain.model.MessageTypeEnum
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.parseDateForChat
import com.edu.common.utils.setBackground


class ChatMembersAdapter(
    private val imageLoader: IImageLoader,
    private val listener: ChatMemberClickListener
) :
    ListAdapter<ChatMemberItem, ChatMembersAdapter.ChatMemberVH>(object :
        DiffUtil.ItemCallback<ChatMemberItem>() {
        override fun areItemsTheSame(oldItem: ChatMemberItem, newItem: ChatMemberItem): Boolean {
            return oldItem.chatUserUid == newItem.chatUserUid
        }

        override fun areContentsTheSame(
            oldItem: ChatMemberItem,
            newItem: ChatMemberItem
        ): Boolean {
            return oldItem == newItem
        }
    }) {


    inner class ChatMemberVH(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.lastMessage.text = this.message
                binding.status.isVisible = true
                binding.messageType.isVisible = true

                if (this.messageAuthor == MessageAuthorEnum.ME) {
                    when (MessageStatusEnum.getByValue(lastMessageStatus)) {
                        MessageStatusEnum.NOT_SENT -> binding.status.setBackground(R.drawable.ic_baseline_access_time_24)
                        MessageStatusEnum.SEEN -> binding.status.setBackground(R.drawable.ic_check_group)
                        MessageStatusEnum.SENT -> binding.status.setBackground(R.drawable.ic_check)
                    }
                } else {
                    binding.status.isVisible = false
                }
                when (messageType) {
                    MessageTypeEnum.Document -> {
                        binding.messageType.setBackground(R.drawable.ic_fluent_document_24_filled)
                    }
                    MessageTypeEnum.Image -> {
                        binding.messageType.setBackground(R.drawable.ic_bxs_camera)
                    }
                    MessageTypeEnum.Text -> {
                        binding.messageType.isVisible = false
                    }
                }
                binding.lastMessageTime.text = this.time?.parseDateForChat()
                imageLoader.loadImageWithCircleShape(
                    chatUserAvatar,
                    binding.logo,
                    R.drawable.ic_chat_item
                )
                binding.studentName.text = chatUserName
                binding.newMessagesCount.isVisible = newMessagesCount > 0
                binding.newMessagesCount.text = newMessagesCount.toString()
                binding.root.setOnClickListener {
                    listener.click(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMemberVH {
        return ChatMemberVH(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatMemberVH, position: Int) {
        holder.bind(position)
    }

    interface ChatMemberClickListener {
        fun click(chatMember: ChatMemberItem)
    }

}