package com.edu.chat.presentation

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.edu.chat.R
import com.edu.chat.databinding.FragmentChatDetailsBinding
import com.edu.chat.domain.model.MessageTypeEnum
import com.edu.chat.presentation.adapter.ChatMessagesAdapter
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.model.NetworkStatus
import com.edu.common.utils.NetworkStatusHelper
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatDetailFragment : BaseFragment<ChatDetailsViewModel, FragmentChatDetailsBinding>(
    R.layout.fragment_chat_details,
    FragmentChatDetailsBinding::bind
) {

    @Inject
    lateinit var imageLoader: IImageLoader

    override val viewModel: ChatDetailsViewModel by viewModels()

    private val args by navArgs<ChatDetailFragmentArgs>()

    private var firstMessages: Boolean = true

    private val adapter by lazy {
        ChatMessagesAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getProfileInfo(args.userId)
        viewModel.getOrCreateChannel(args.userId)
    }

    override fun progressLoader(show: Boolean) {
        binding.loader.root.isVisible = show
    }

    override fun setupUI() {
        super.setupUI()
        binding.toolbar.setupWithNavController(findNavController())
        binding.recyclerView.apply {
            adapter = this@ChatDetailFragment.adapter
        }
        binding.sendBtn.setOnClickListener {
            if (binding.messageText.text.isNullOrEmpty()){
                showToast(resources.getString(R.string.message_should_not_be_empty))
                return@setOnClickListener
            }
            viewModel.sendMessage(
                binding.messageText.text.toString(),
                args.userId,
                MessageTypeEnum.Text
            )
            binding.messageText.text.clear()
        }
        binding.recyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.recyclerView.scrollBy(0, oldBottom - bottom)
            }
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1) && dy < 0){
                    viewModel.getNextPageOfMessages(args.userId)
                }
            }
        })

    }

    override fun setupVM() {
        super.setupVM()
        viewModel.chatMessages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
            if (firstMessages) {
                firstMessages = !firstMessages
                binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }
        viewModel.chatMemberInfo.observe(viewLifecycleOwner) {
            binding.userName.text = it.name
            imageLoader.loadImageWithCircleShape(
                it.avatarUrl,
                binding.userLogo,
                R.drawable.ic_chat_item
            )
        }

        NetworkStatusHelper(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkStatus.Available -> {
                    viewModel.listenToChatMessagesAgain()
                }
                is NetworkStatus.UnAvailable -> {}
            }
        }
    }


}