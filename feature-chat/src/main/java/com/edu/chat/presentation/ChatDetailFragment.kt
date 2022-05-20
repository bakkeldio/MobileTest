package com.edu.chat.presentation

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.edu.chat.R
import com.edu.chat.databinding.FragmentChatDetailsBinding
import com.edu.chat.domain.model.MessageTypeEnum
import com.edu.chat.presentation.adapter.ChatMessagesAdapter
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.SelectImageBottomSheetDialog
import com.edu.common.presentation.model.CropImageShapeEnum
import com.edu.common.presentation.model.NetworkStatus
import com.edu.common.utils.*
import com.edu.common.utils.imageLoading.IImageLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatDetailFragment : BaseFragment<ChatDetailsViewModel, FragmentChatDetailsBinding>(
    R.layout.fragment_chat_details,
    FragmentChatDetailsBinding::bind
), ChatMediaDocumentsSelectBottomSheetFragment.Listener, SelectImageBottomSheetDialog.Listener,
    ChatMessagesAdapter.Listener {

    @Inject
    lateinit var imageLoader: IImageLoader

    override val viewModel: ChatDetailsViewModel by viewModels()

    private val args by navArgs<ChatDetailFragmentArgs>()

    private var firstMessages: Boolean = true

    private var bottomSheetFragment: ChatMediaDocumentsSelectBottomSheetFragment? = null

    private var imageCropBottomSheetFragment: SelectImageBottomSheetDialog? = null


    private val selectDocument =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val newUri = createFileFromUri(it)
                viewModel.sendFile(newUri.toString(), args.userId, MessageTypeEnum.Document)
            }
        }

    private val chooseImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageCropBottomSheetFragment =
                    SelectImageBottomSheetDialog(uri, this, CropImageShapeEnum.RECTANGLE)
                imageCropBottomSheetFragment?.show(
                    childFragmentManager,
                    SelectImageBottomSheetDialog.TAG
                )
            }
        }

    private val selectContact =
        registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->

        }


    private val makePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                fileUri?.let { uri ->
                    imageCropBottomSheetFragment =
                        SelectImageBottomSheetDialog(uri, this, CropImageShapeEnum.RECTANGLE)
                    imageCropBottomSheetFragment?.show(
                        childFragmentManager,
                        SelectImageBottomSheetDialog.TAG
                    )
                }
            }
        }

    private var fileUri: Uri? = null


    private val adapter by lazy {
        ChatMessagesAdapter(imageLoader, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getOrCreateChannel(args.userId)
    }

    override fun progressLoader(show: Boolean) {
        binding.loader.root.isVisible = show
    }

    override fun setupUI() {
        setAdjustResizeSoftInput()
        binding.toolbar.setupWithNavController(findNavController())
        binding.recyclerView.apply {
            adapter = this@ChatDetailFragment.adapter
        }
        binding.sendBtn.setOnClickListener {
            if (binding.messageText.text.isNullOrEmpty()) {
                showToast(resources.getString(R.string.message_should_not_be_empty))
                return@setOnClickListener
            }
            viewModel.sendTextMessage(
                binding.messageText.text.toString(),
                args.userId
            )
            binding.messageText.text.clear()
        }
        binding.recyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.recyclerView.scrollBy(0, oldBottom - bottom)
            }
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                    viewModel.getNextPageOfMessages(args.userId)
                }
            }
        })
        binding.shareBtn.setOnClickListener {
            bottomSheetFragment = ChatMediaDocumentsSelectBottomSheetFragment(this)
            bottomSheetFragment?.show(
                childFragmentManager,
                ChatMediaDocumentsSelectBottomSheetFragment.TAG
            )
        }

    }

    override fun setupVM() {
        super.setupVM()
        viewModel.uploadingOfFilesLiveData.observe(viewLifecycleOwner) { workInfos ->
            workInfos.forEach { info ->
                when (info.state) {
                    WorkInfo.State.RUNNING -> {
                        info.progress.getString("messageUid")?.let {
                            viewModel.updateStateOfImageMessage(it, true)
                        }
                    }
                    WorkInfo.State.FAILED -> {
                        info.outputData.getString("messageUid")?.let {
                            viewModel.updateStateOfImageMessage(it, false)
                        }
                    }
                    else -> Unit
                }
            }
        }
        viewModel.downloadingOfFilesLiveData.observe(viewLifecycleOwner) { infos ->
            infos.forEach { info ->
                when (info.state) {
                    WorkInfo.State.RUNNING -> {
                        info.progress.getString("messageUid")?.let {
                            viewModel.updateProgressOfDownloadingFile(it, true)
                        }
                    }
                    WorkInfo.State.FAILED -> {
                        showToast(info.outputData.getString("exception") ?: "")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        info.outputData.getString("messageUid")?.let {
                            viewModel.updateProgressOfDownloadingFile(it, false)
                        }
                    }
                    else -> Unit
                }
            }
        }
        viewModel.chatMessages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)

                binding.recyclerView.scrollToPosition(adapter.itemCount - 1)

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
                    //viewModel.listenToChatMessagesAgain()
                }
                is NetworkStatus.UnAvailable -> {
                }
            }
        }
    }

    override fun makePhoto() {
        bottomSheetFragment?.dismiss()
        fileUri = createTempFileAndGetUri()
        makePhoto.launch(fileUri)
    }

    override fun selectDocument() {
        bottomSheetFragment?.dismiss()
        selectDocument.launch(arrayOf("application/msword", "application/pdf"))
    }

    override fun selectPhoto() {
        bottomSheetFragment?.dismiss()
        chooseImage.launch("image/*")
    }

    override fun selectContact() {
        bottomSheetFragment?.dismiss()
        selectContact.launch(null)
    }

    override fun getCroppedImage(fileName: String, bitmap: Bitmap) {
        imageCropBottomSheetFragment?.dismiss()
        val uri = convertBitmapToUri(lifecycleScope, fileName, bitmap)
        viewModel.sendFile(
            uri.toString(),
            args.userId,
            MessageTypeEnum.Image
        )

    }

    override fun retryImageUpload(messageUid: String, messageUri: String) {
        viewModel.retryImageUploadWork(messageUid, messageUri)
    }

    override fun openFileContent(uri: Uri) {
        openFile(uri.toFile())
    }


}