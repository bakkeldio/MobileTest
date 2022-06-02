package com.edu.test.presentation.newQuestion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.edu.common.domain.model.QuestionType
import com.edu.common.utils.hideSoftKeyboard
import com.edu.common.utils.showToast
import com.edu.test.R
import com.edu.test.databinding.FragmentNewQuestionBinding
import com.edu.test.domain.model.CreateQuestionDomain
import com.edu.test.domain.model.dbModels.AnswersList
import com.edu.test.domain.model.dbModels.QuestionAnswerDomain
import com.edu.test.domain.model.dbModels.QuestionDomain
import com.edu.test.domain.model.dbModels.QuestionWithAnswersDomain
import com.edu.test.presentation.adapter.CreateAnswersAdapter
import com.edu.test.presentation.adapter.SwipeHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewQuestionFragment : Fragment(), CreateAnswersAdapter.MarkAsCorrectClickListener,
    NewAnswerBottomSheetFragment.ClickListener {

    private var _binding: FragmentNewQuestionBinding? = null

    private val binding: FragmentNewQuestionBinding get() = _binding!!

    private var dialogFragment: NewAnswerBottomSheetFragment? = null

    private var answers: MutableList<QuestionAnswerDomain> = mutableListOf()

    private val viewModel by viewModels<NewQuestionViewModel>()

    companion object {
        const val QUESTION_KEY = "QUESTION_KEY"
        const val QUESTION_MODEL = "QUESTION_MODEL"
        fun createInstance(bundle: Bundle): NewQuestionFragment {
            val instance = NewQuestionFragment()
            instance.arguments = bundle
            return instance
        }
    }

    private val answersAdapter by lazy {
        CreateAnswersAdapter(this)
    }

    private val savedQuestion by lazy {
        requireArguments().getParcelable<QuestionDomain>("question")!!
    }

    private val itemTouchListener by lazy {
        ItemTouchHelper(object : SwipeHelper(requireContext(), binding.answersRv) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                return arrayListOf(
                    UnderlayButton(
                        requireContext(),
                        imageResId = R.drawable.ic_baseline_delete_24,
                        color = R.color.main_color_pink,
                        clickListener = {
                            setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
                            viewModel.deleteAnswer(answers[position])
                        }
                    ),
                    UnderlayButton(
                        requireContext(),
                        imageResId = R.drawable.ic_baseline_edit_24,
                        color = R.color.green_400,
                        clickListener = {
                            buildDialogWithText(
                                answersAdapter.currentList[position].title,
                                position
                            )
                        }
                    )
                )
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSavedQuestionData(savedQuestion)
        binding.questionEditText.setOnEditorActionListener { _, actionId, eventKey ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideFocusAndKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.questionTypesRadioGroup.setOnCheckedChangeListener { _, _ ->
            hideFocusAndKeyboard()
            setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
            when (getQuestionType()) {
                QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS -> {
                    manageViewsVisibility(true)
                }
                QuestionType.MULTIPLE_CHOICE_ONE_ANSWER -> {
                    val existingAnswers = answers
                    answers.clear()
                    answers.addAll(existingAnswers.map {
                        it.copy(isCorrect = false)
                    }
                    )
                    answersAdapter.submitList(answers.toList())
                    manageViewsVisibility(true)
                }
                QuestionType.OPEN -> {
                    manageViewsVisibility(false)
                }
            }
        }

        binding.answersRv.apply {
            adapter = answersAdapter
            itemTouchListener.attachToRecyclerView(this)
        }
        binding.addAnswerBtn.setOnClickListener {
            buildDialog()
        }
        setPoint()
        addListenerForQuestionChange()
        binding.updateQuestionBtn.setOnClickListener {
            if (validateQuestion()) {
                if (getQuestionType() == QuestionType.OPEN) {
                    answers.clear()

                }
                showToast(resources.getString(R.string.saved))
                setFragmentResult(
                    QUESTION_KEY,
                    bundleOf(
                        QUESTION_MODEL to
                                CreateQuestionDomain(
                                    questionType = getQuestionType().type,
                                    question = binding.questionEditText.text.toString(),
                                    point = binding.pointTextView.text.toString().toInt(),
                                    id = savedQuestion.id
                                ), "change" to false
                    )
                )
            }
        }
        binding.deleteQuestionBtn.setOnClickListener {
            setFragmentResult(QUESTION_KEY, bundleOf(QUESTION_KEY to savedQuestion.id))
        }

        viewModel.answers.observe(viewLifecycleOwner) {
            answers = it.toMutableList()
            answersAdapter.submitList(it)
        }

    }

    fun getSavedQuestionData(question: QuestionDomain) {
        viewModel.getQuestionAnswers(question.id)
        binding.questionEditText.setText(question.title)
        binding.pointTextView.text = question.point.toString()
        binding.questionTypesRadioGroup.check(getCheckedRadioButtonId(question.questionType))
    }

    private fun hideFocusAndKeyboard() {
        binding.questionEditText.hideSoftKeyboard()
    }

    private fun setPoint() {
        binding.minusBtn.setOnClickListener {
            setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
            val point = binding.pointTextView.text.toString().toInt() - 1
            binding.pointTextView.text = point.toString()
        }
        binding.addBtn.setOnClickListener {
            setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
            val point = binding.pointTextView.text.toString().toInt() + 1
            binding.pointTextView.text = point.toString()
        }
    }

    private fun addListenerForQuestionChange() {
        binding.questionEditText.doAfterTextChanged { editable ->
            editable?.let {
                if (binding.questionEditText.hasFocus()) {
                    binding.questionTextInputLayout.error = null
                    setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
                }
            }
        }
    }

    private fun manageViewsVisibility(isVisible: Boolean) {
        binding.addAnswerBtn.isVisible = isVisible
        binding.answersRv.isVisible = isVisible
    }

    private fun validateQuestion(): Boolean {
        var isValid = true
        if (binding.questionEditText.text.isNullOrEmpty()) {
            isValid = false
            binding.questionTextInputLayout.error =
                resources.getString(R.string.question_field_must_not_be_null)
        }
        if (binding.questionTypesRadioGroup.checkedRadioButtonId == R.id.withOneCorrectAnswer || binding.questionTypesRadioGroup.checkedRadioButtonId == R.id.withMultipleCorrectAnswers) {
            if (binding.answersRv.adapter?.itemCount ?: 0 < 2) {
                showToast(resources.getString(R.string.possible_answers_not_enough))
                return false
            }
        }
        if (answers.count {
                it.isCorrect
            } == 0 && (getQuestionType() == QuestionType.MULTIPLE_CHOICE_ONE_ANSWER || getQuestionType() == QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS)) {
            showToast(resources.getString(R.string.choose_correct_answer_for_given_question))
            return false
        }
        return isValid
    }

    private fun getQuestionType(): QuestionType {
        return when (binding.questionTypesRadioGroup.checkedRadioButtonId) {
            R.id.withOneCorrectAnswer -> QuestionType.MULTIPLE_CHOICE_ONE_ANSWER
            R.id.withMultipleCorrectAnswers -> QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS
            R.id.openQuestion -> QuestionType.OPEN
            else -> QuestionType.MULTIPLE_CHOICE_ONE_ANSWER
        }
    }

    private fun getCheckedRadioButtonId(questionType: QuestionType): Int {
        return when (questionType) {
            QuestionType.OPEN -> R.id.openQuestion
            QuestionType.MULTIPLE_CHOICE_ONE_ANSWER -> R.id.withOneCorrectAnswer
            QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS -> R.id.withMultipleCorrectAnswers
        }
    }

    private fun buildDialog() {
        dialogFragment = NewAnswerBottomSheetFragment(this, position = answersAdapter.itemCount)
        dialogFragment?.show(parentFragmentManager, "")
    }

    private fun buildDialogWithText(text: String, position: Int) {
        dialogFragment = NewAnswerBottomSheetFragment(this, text, position)
        dialogFragment?.show(parentFragmentManager, "")
    }

    override fun addAnswer(text: String, position: Int) {
        setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
        viewModel.insertAnswer(
            QuestionAnswerDomain(
                title = text,
                isCorrect = false,
                questionId = savedQuestion.id
            )
        )

        dialogFragment?.dismiss()
    }

    override fun updateAnswer(text: String, position: Int) {
        setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
        answers[position] = answers[position].copy(title = text)
        viewModel.updateAnswer(answers[position])
        dialogFragment?.dismiss()
    }

    override fun longClick(model: QuestionAnswerDomain, position: Int) {
        if (answers[position].isCorrect) {
            answers[position] = answers[position].copy(isCorrect = false)
            viewModel.updateAnswer(answers[position])
        } else {
            when (getQuestionType()) {
                QuestionType.MULTIPLE_CHOICE_ONE_ANSWER -> {
                    if (answers.count { it.isCorrect } == 0) {
                        answers[position] = answers[position].copy(isCorrect = true)
                        viewModel.updateAnswer(answers[position])
                    }
                }
                QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS -> {
                    answers[position] = answers[position].copy(isCorrect = true)
                    viewModel.updateAnswer(answers[position])
                }
                QuestionType.OPEN -> Unit
            }
        }
    }

}