package com.edu.chat.presentation.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.chat.R
import com.edu.chat.databinding.ItemChatTimeHeaderBinding
import com.edu.chat.databinding.ItemReceivedMessageBinding
import com.edu.chat.databinding.ItemSentMessageBinding
import com.edu.chat.domain.model.MessageStatusEnum
import com.edu.chat.domain.model.MessageTypeEnum
import com.edu.chat.presentation.model.ChatMessage
import com.edu.common.utils.getTimeFromDate
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.rightDrawable
import com.edu.common.utils.setColorForText

class ChatMessagesAdapter(
    private val imageLoader: IImageLoader,
    private val listener: Listener
) :
    ListAdapter<ChatMessage, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.message == newItem.message && if (oldItem is ChatMessage.SentMessage && newItem is ChatMessage.SentMessage) {
                oldItem.showRetry == newItem.showRetry && oldItem.imageIsUploaded == newItem.imageIsUploaded
            } else if (oldItem is ChatMessage.ReceivedMessage && newItem is ChatMessage.ReceivedMessage) {
                oldItem.imageIsDownloaded == newItem.imageIsDownloaded
            } else {
                true
            }

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
        if (holder is BaseViewHolder) {
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
            (getItem(position) as ChatMessage.ReceivedMessage).apply {

                binding.image.isVisible = MessageTypeEnum.Image.value == message.messageType
                binding.receivedMessageText.isVisible =
                    MessageTypeEnum.Text.value == message.messageType
                binding.documentGroup.isVisible =
                    message.messageType == MessageTypeEnum.Document.value

                when (MessageTypeEnum.getTypeByValue(message.messageType)) {
                    MessageTypeEnum.Image -> {
                        binding.time.updatePadding(right = 8, bottom = 5)
                        binding.time.setColorForText(R.color.white)
                        imageLoader.loadImage(message.messageUrl, binding.image)
                    }
                    MessageTypeEnum.Text -> {
                        binding.time.updatePadding(right = 0, bottom = 0)
                        binding.time.setColorForText(R.color.gray_717677)
                        binding.receivedMessageText.text = message.message
                        changeLayoutConstraint(
                            binding.receivedMessageText,
                            binding.receivedMessageLayout
                        )
                    }
                    MessageTypeEnum.Document -> {
                        binding.documentLayout.root.background = ContextCompat.getDrawable(
                            binding.root.context,
                            R.drawable.bg_item_received_document
                        )
                        binding.documentLayout.documentText.text = message.fileName
                        binding.documentType.text = message.fileExtension
                        binding.documentSize.text = message.fileSize
                        binding.documentLayout.progressBar.isVisible = imageIsDownloaded
                        binding.documentLayout.documentText.setOnClickListener {
                            message.receiverFileUri?.let { uri ->
                                listener.openFileContent(Uri.parse(uri))
                            }
                        }
                    }
                }
                binding.time.text = message.time.getTimeFromDate()
            }
        }
    }

    inner class SentMessageVH(val binding: ItemSentMessageBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(position: Int) {
            (getItem(position) as ChatMessage.SentMessage).apply {

                binding.sentMessageText.isVisible =
                    message.messageType == MessageTypeEnum.Text.value
                binding.image.isVisible = message.messageType == MessageTypeEnum.Image.value
                binding.retryBtn.isVisible =
                    showRetry && message.messageType != MessageTypeEnum.Document.value
                binding.documentGroup.isVisible =
                    message.messageType == MessageTypeEnum.Document.value
                when (MessageTypeEnum.getTypeByValue(message.messageType)) {
                    MessageTypeEnum.Text -> {
                        binding.time.updatePadding(right = 0, bottom = 0)
                        binding.time.setColorForText(R.color.gray_717677)
                        binding.progressBar.isVisible = false
                        binding.sentMessageText.text = message.message

                        changeLayoutConstraint(
                            binding.sentMessageText,
                            binding.messageLayout
                        )
                    }
                    MessageTypeEnum.Image -> {
                        binding.time.updatePadding(right = 8, bottom = 5)
                        binding.progressBar.isVisible =
                            imageIsUploaded && message.status == MessageStatusEnum.NOT_SENT.value

                        binding.time.setColorForText(R.color.white)
                        if (MessageStatusEnum.getByValue(message.status) == MessageStatusEnum.NOT_SENT) {
                            message.senderFileUri?.let { uri ->
                                imageLoader.loadImageFromUri(Uri.parse(uri), binding.image)
                            }
                        } else {
                            imageLoader.loadImage(message.messageUrl, binding.image)
                        }
                    }
                    MessageTypeEnum.Document -> {
                        binding.progressBar.isVisible = false
                        val uri = Uri.parse(message.senderFileUri)
                        val file = uri.toFile()
                        binding.time.setColorForText(R.color.gray_717677)
                        binding.documentLayout.documentText.text = file.name
                        binding.documentType.text = file.extension
                        binding.documentSize.text = file.length().toString()

                        binding.documentLayout.uploadBtn.isVisible = showRetry
                        binding.documentLayout.progressBar.isVisible =
                            imageIsUploaded && message.status == MessageStatusEnum.NOT_SENT.value
                        binding.documentLayout.documentText.setOnClickListener {
                            message.senderFileUri?.let { uri ->
                                listener.openFileContent(Uri.parse(uri))
                            }
                        }

                    }
                }
                binding.retryBtn.setOnClickListener {
                    message.senderFileUri?.let { uri ->
                        listener.retryImageUpload(message.uid, uri)
                    }
                }
                binding.time.text = message.time.getTimeFromDate()

                setStatusForMessage(
                    MessageStatusEnum.getByValue(message.status),
                    MessageTypeEnum.getTypeByValue(message.messageType),
                    binding.time
                )
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

    fun setStatusForMessage(
        messageStatusEnum: MessageStatusEnum,
        messageTypeEnum: MessageTypeEnum,
        timeTextView: TextView
    ) {
        when (messageStatusEnum) {
            MessageStatusEnum.NOT_SENT -> if (messageTypeEnum != MessageTypeEnum.Image) timeTextView.rightDrawable(
                R.drawable.ic_baseline_access_time_24
            ) else timeTextView.rightDrawable(R.drawable.ic_baseline_access_time_24_white)
            MessageStatusEnum.SENT -> when (messageTypeEnum) {
                MessageTypeEnum.Image -> timeTextView.rightDrawable(R.drawable.ic_check_white)
                MessageTypeEnum.Document -> timeTextView.rightDrawable(
                    R.drawable.ic_check
                )
                MessageTypeEnum.Text -> timeTextView.rightDrawable(
                    R.drawable.ic_check
                )
            }
            MessageStatusEnum.SEEN -> timeTextView.rightDrawable(R.drawable.ic_check_group)
        }
    }

    fun changeLayoutConstraint(
        messageTextView: TextView,
        messageLayout: ConstraintLayout,
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(messageLayout)
        if (messageTextView.text.length > MESSAGE_LENGTH) {
            constraintSet.clear(R.id.sentMessageText, ConstraintSet.END)
            constraintSet.applyTo(messageLayout)
        } else {
            constraintSet.connect(
                R.id.sentMessageText,
                ConstraintSet.END,
                R.id.time,
                ConstraintSet.START
            )
            constraintSet.applyTo(messageLayout)
        }
    }

    abstract class BaseViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(position: Int)
    }

    interface Listener {
        fun retryImageUpload(messageUid: String, messageUri: String)
        fun openFileContent(uri: Uri)
    }

}