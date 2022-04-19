package com.edu.test.presentation.testResult

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edu.common.utils.viewBinding
import com.edu.test.R
import com.edu.test.databinding.FragmentChangeOpenQuestionScoreBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeOpenQuestionScoreBottomSheetFragment(private val score: Int, private val listener: Listener) :
    BottomSheetDialogFragment() {

    val binding by viewBinding(FragmentChangeOpenQuestionScoreBottomsheetBinding::bind)

    companion object {
        const val TAG = "CHANGE_SCORE_FRAGMENT"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_change_open_question_score_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.score.setText(score.toString())
        binding.updateBtn.setOnClickListener {
            listener.updateScore(binding.score.text.toString().toInt())
        }

        binding.minusBtn.setOnClickListener {
            binding.score.setText((binding.score.text.toString().toInt() - 1).toString())
        }
        binding.plusBtn.setOnClickListener {
            val newScore = binding.score.text.toString().toInt() + 1
            binding.score.setText(newScore.toString())
        }

    }

    interface Listener {
        fun updateScore(newScore: Int)
    }

}