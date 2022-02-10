package com.example.test.presentation.newQuestion

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test.R
import com.example.test.databinding.FragmentAddNewAnswerBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewAnswerBottomSheetFragment(
    private val clickListener: ClickListener,
    private val text: String? = null,
    private val position: Int = 0
) : BottomSheetDialogFragment() {

    private var _binding: FragmentAddNewAnswerBottomsheetBinding? = null

    private val binding: FragmentAddNewAnswerBottomsheetBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewAnswerBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
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
        text?.let {
            binding.answer.setText(it)
            binding.addBtn.text = resources.getString(R.string.update_answer_text)
        }
        binding.addBtn.setOnClickListener {
            text?.let {
                clickListener.updateAnswer(binding.answer.text.toString(), position = position)
            } ?: clickListener.addAnswer(binding.answer.text.toString(), position)
        }
    }

    interface ClickListener {
        fun addAnswer(text: String, position: Int)
        fun updateAnswer(text: String, position: Int)
    }
}