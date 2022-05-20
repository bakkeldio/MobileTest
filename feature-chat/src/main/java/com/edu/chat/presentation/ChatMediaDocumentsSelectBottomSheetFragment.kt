package com.edu.chat.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edu.chat.R
import com.edu.chat.databinding.FragmentChatMediaDocumentsSelectBottomsheetBinding
import com.edu.common.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChatMediaDocumentsSelectBottomSheetFragment(private val listener: Listener) :
    BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentChatMediaDocumentsSelectBottomsheetBinding::bind)


    companion object {
        const val TAG = "CHAT_MEDIA_DOCUMENT_SELECT_BOTTOM_SHEET"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_chat_media_documents_select_bottomsheet, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.makePhotoBtn.setOnClickListener {
            listener.makePhoto()
        }

        binding.selectDocumentBtn.setOnClickListener {
            listener.selectDocument()
        }

        binding.selectPhotoBtn.setOnClickListener {
            listener.selectPhoto()
        }

        binding.selectContactBtn.setOnClickListener {
            listener.selectContact()
        }
    }


    interface Listener {
        fun makePhoto()
        fun selectDocument()
        fun selectPhoto()
        fun selectContact()
    }
}