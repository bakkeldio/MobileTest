package com.edu.chat.presentation

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.edu.chat.R
import com.edu.chat.databinding.FragmentChatBinding
import com.edu.chat.domain.model.ChatMember
import com.edu.chat.domain.model.ChatMemberItem
import com.edu.chat.presentation.adapter.ChatMembersAdapter
import com.edu.chat.presentation.adapter.ChatNonMembersAdapter
import com.edu.common.presentation.BaseFragment
import com.edu.common.utils.DebounceQueryTextListener
import com.edu.common.utils.imageLoading.IImageLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<ChatViewModel, FragmentChatBinding>(
    R.layout.fragment_chat,
    FragmentChatBinding::bind
), ChatMembersAdapter.ChatMemberClickListener, ChatNonMembersAdapter.ItemClickListener {

    override val viewModel: ChatViewModel by viewModels()

    @Inject
    lateinit var imageLoader: IImageLoader

    private val adapter: ChatMembersAdapter by lazy {
        ChatMembersAdapter(imageLoader, this)
    }

    private val adapterNonMembers: ChatNonMembersAdapter by lazy {
        ChatNonMembersAdapter(this, imageLoader)
    }

    private val concatAdapter by lazy {
        ConcatAdapter(adapter, adapterNonMembers)
    }

    override fun setupUI() {
        super.setupUI()
        binding.recyclerView.adapter = concatAdapter
        binding.searchView.setOnQueryTextListener(DebounceQueryTextListener {
            if (binding.searchView.hasFocus()) {
                if (it.isEmpty()) {
                    viewModel.resetChatMembers()
                } else {
                    viewModel.searchThroughChatsMembers(it)
                    viewModel.searchThroughNonChats(it)
                }
            }
        })
    }

    override fun setupVM() {
        super.setupVM()
        viewModel.chatMembers.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.nonMembers.observe(viewLifecycleOwner) {
            adapterNonMembers.submitList(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("USER_ID")?.let {
            findNavController().navigate(ChatFragmentDirections.chatDetailsFragment(it))
        }
        viewModel.getChatMembers()
    }

    override fun progressLoader(show: Boolean) {
        binding.progress.root.isVisible = show
    }

    override fun click(chatMember: ChatMemberItem) {
        findNavController().navigate(
            ChatFragmentDirections.actionFromChatFragmentToChatDetailsFragment(
                chatMember.chatUserUid
            )
        )
    }

    override fun onClick(member: ChatMember) {
        findNavController().navigate(
            ChatFragmentDirections.actionFromChatFragmentToChatDetailsFragment(
                member.uid
            )
        )
    }
}