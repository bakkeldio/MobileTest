package com.edu.chat.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.chat.R
import com.edu.chat.databinding.ItemChatTimeHeaderBinding
import com.edu.chat.databinding.ItemReceivedMessageBinding
import com.edu.chat.databinding.ItemSentMessageBinding
import com.edu.chat.domain.model.MessageStatusEnum
import com.edu.chat.presentation.model.ChatMessage
import com.edu.common.utils.getTimeFromDate
import com.edu.common.utils.rightDrawable

class ChatMessagesAdapter :
    ListAdapter<ChatMessage, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.message == newItem.message
        }

    }) {


    companion object {
        private const val RECEIVED_MESSAGE_TYPE = 0
        private const val SENT_MESSAGE_TYPE = 1
        private const val HEADER_TYPE = 2
        private const val MESSAGE_LENGTH = 30
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RECEIVED_MESSAGE_TYPE -> {
                ReceivedMessageVH(
                    ItemReceivedMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            SENT_MESSAGE_TYPE -> {
                SentMessageVH(
                    ItemSentMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                HeaderVH(
                    ItemChatTimeHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BaseViewHolder){
            holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatMessage.ReceivedMessage -> RECEIVED_MESSAGE_TYPE
            is ChatMessage.SentMessage -> SENT_MESSAGE_TYPE
            is ChatMessage.Header -> HEADER_TYPE
        }
    }

    inner class ReceivedMessageVH(private val binding: ItemReceivedMessageBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(position: Int) {
            getItem(position).apply {
                binding.receivedMessageText.text = message.message
                binding.time.text = message.time.getTimeFromDate()
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.receivedMessageLayout)
                if (binding.receivedMessageText.text.length > MESSAGE_LENGTH) {
                    constraintSet.clear(R.id.sentMessageText, ConstraintSet.END)
                    constraintSet.applyTo(binding.receivedMessageLayout)
                } else {
                    constraintSet.connect(
                        R.id.sentMessageText,
                        ConstraintSet.END,
                        R.id.time,
                        ConstraintSet.START
                    )
                    constraintSet.applyTo(binding.receivedMessageLayout)
                }
            }
        }
    }

    inner class SentMessageVH(val binding: ItemSentMessageBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(position: Int) {
            getItem(position).apply {
                binding.sentMessageText.text = message.message
                binding.time.text = message.time.getTimeFromDate()
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.messageLayout)

                when(MessageStatusEnum.getByValue(message.status)){
                    MessageStatusEnum.NOT_SENT -> binding.time.rightDrawable(R.drawable.ic_baseline_access_time_24)
                    MessageStatusEnum.SENT -> binding.time.rightDrawable(R.drawable.ic_check)
                    MessageStatusEnum.SEEN -> binding.time.rightDrawable(R.drawable.ic_check_group)
                }

                if (binding.sentMessageText.text.length > MESSAGE_LENGTH) {
                    constraintSet.clear(R.id.sentMessageText, ConstraintSet.END)
                    constraintSet.applyTo(binding.messageLayout)
                } else {
                    constraintSet.connect(
                        R.id.sentMessageText,
                        ConstraintSet.END,
                        R.id.time,
                        ConstraintSet.START
                    )
                    constraintSet.applyTo(binding.messageLayout)
                }

            }
        }
    }

    inner class HeaderVH(val binding: ItemChatTimeHeaderBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(position: Int) {
            getItem(position).apply {
                if (this is ChatMessage.Header) {
                    binding.time.isVisible = true
                    binding.time.text = date
                }
            }
        }
    }

    abstract class BaseViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        abstract fun bind(position: Int)
    }


}