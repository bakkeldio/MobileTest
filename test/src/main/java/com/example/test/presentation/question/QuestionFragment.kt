package com.example.test.presentation.question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.common.domain.model.QuestionDomain
import com.example.common.domain.model.QuestionType
import com.example.test.databinding.FragmentQuestionBinding

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun createInstance(bundle: Bundle): QuestionFragment {
            val instance = QuestionFragment()
            instance.arguments = bundle
            return instance
        }

        const val ONE = 1
        const val TWO = 2
        const val THREE = 3
        const val FOUR = 4
    }

    private val questionModel by lazy {
        requireArguments().getParcelable<QuestionDomain>("question")
    }

    private val position by lazy {
        requireArguments().getInt("position")
    }

    var toCheck = false

    private val checkedCheckBoxTexts: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.questionTextView.text = "${position + 1}. ${questionModel?.question}"


        binding.radioGroupLayout.root.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<RadioButton>(i)


            setFragmentResult(
                "result",
                bundleOf(
                    "page" to position,
                    "questionId" to questionModel?.uid,
                    "answer" to radioButton.text.toString().substringBefore("."),
                    "questionType" to QuestionType.MULTIPLE_CHOICE_ONE_ANSWER.type
                )
            )
        }
        binding.checkboxLayout.root.forEach {
            val checkBox = it as? CheckBox
            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedCheckBoxTexts.add(checkBox.text.toString().substringBefore("."))
                } else {
                    checkedCheckBoxTexts.remove(checkBox.text.toString().substringBefore("."))
                }
                this.toCheck = checkedCheckBoxTexts.size > 0
                setFragmentResult(
                    "result",
                    bundleOf(
                        "page" to position,
                        "questionId" to questionModel?.uid,
                        "checkedAnswers" to checkedCheckBoxTexts,
                        "toCheck" to this.toCheck,
                        "questionType" to QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS.type
                    )
                )
            }

        }

        when (questionModel?.questionType) {
            QuestionType.MULTIPLE_CHOICE_ONE_ANSWER -> {
                setupMultipleChoice(questionModel?.answersList ?: hashMapOf())
            }
            QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS -> {
                setupMultipleChoice(questionModel?.answersList ?: hashMapOf(), true)
            }
            QuestionType.OPEN -> {

            }
        }
    }

    private fun setupMultipleChoice(answers: HashMap<String, String>, isCheckBox: Boolean = false) {
        when (answers.size) {
            TWO -> {
                if (isCheckBox) {
                    binding.checkboxLayout.thirdCheckBx.isVisible = false
                    binding.checkboxLayout.fourthCheckBx.isVisible = false
                } else {
                    binding.radioGroupLayout.thirdBtn.isVisible = false
                    binding.radioGroupLayout.fourthBtn.isVisible = false
                }
            }
            THREE -> {
                if (isCheckBox) {
                    binding.checkboxLayout.fourthCheckBx.isVisible = false
                } else {
                    binding.radioGroupLayout.fourthBtn.isVisible = false
                }
            }
        }
        answers.keys.forEachIndexed { index, s ->
            val answer = "$s. ${answers[s]}"
            when (index + 1) {
                ONE -> {
                    if (isCheckBox) {
                        binding.checkboxLayout.firstCheckBx.text = answer
                    } else {
                        binding.radioGroupLayout.firstBtn.text = answer
                    }
                }
                TWO -> {
                    if (isCheckBox) {
                        binding.checkboxLayout.secondCheckBx.text = answer
                    } else {
                        binding.radioGroupLayout.secondBtn.text = answer
                    }
                }
                THREE -> {
                    if (isCheckBox) {
                        binding.checkboxLayout.thirdCheckBx.text = answer
                    } else {
                        binding.radioGroupLayout.thirdBtn.text = answer
                    }
                }
                FOUR -> {
                    if (isCheckBox) {
                        binding.checkboxLayout.fourthCheckBx.text = answer
                    } else {
                        binding.radioGroupLayout.fourthBtn.text = answer
                    }
                }
            }
        }
        binding.checkboxLayout.root.isVisible = isCheckBox
        binding.radioGroupLayout.root.isVisible = !isCheckBox
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}