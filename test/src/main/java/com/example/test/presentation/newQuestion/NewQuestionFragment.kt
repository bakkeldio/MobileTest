package com.example.test.presentation.newQuestion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.test.data.datamanager.TestCreationHandler
import com.example.common.domain.model.QuestionType
import com.example.common.utils.KeyboardManager.hideSoftKeyboard
import com.example.common.utils.showToast
import com.example.test.R
import com.example.test.databinding.FragmentNewQuestionBinding
import com.example.test.presentation.adapter.CreateAnswersAdapter
import com.example.test.presentation.adapter.SwipeHelper
import com.example.test.presentation.model.NewAnswer
import android.view.inputmethod.EditorInfo


class NewQuestionFragment : Fragment(), CreateAnswersAdapter.MarkAsCorrectClickListener,
    NewAnswerBottomSheetFragment.ClickListener {

    private var _binding: FragmentNewQuestionBinding? = null

    private val binding: FragmentNewQuestionBinding get() = _binding!!

    private var dialogFragment: NewAnswerBottomSheetFragment? = null

    private val mapOfCorrectAnswers: HashMap<Int, String> = hashMapOf()

    private val answers: HashMap<Int, String> = hashMapOf()


    companion object {
        const val QUESTION_KEY = "QUESTION_KEY"
        const val QUESTION_MODEL = "QUESTION_MODEL"
    }

    private val answersAdapter by lazy {
        CreateAnswersAdapter(this)
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
                            answers.remove(it)
                            mapOfCorrectAnswers.remove(it)
                            answersAdapter.removeAnswer(it)
                        }
                    ),
                    UnderlayButton(
                        requireContext(),
                        imageResId = R.drawable.ic_baseline_edit_24,
                        color = R.color.green_400,
                        clickListener = {
                            buildDialogWithText(answersAdapter.getAnswerByPosition(it), position)
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
                    mapOfCorrectAnswers.clear()
                    answersAdapter.unMarkAllAnswers()
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
                    mapOfCorrectAnswers.clear()
                    answers.clear()
                }
                showToast(resources.getString(R.string.saved))
                setFragmentResult(
                    QUESTION_KEY,
                    bundleOf(
                        QUESTION_MODEL to
                                TestCreationHandler.mapToCreateQuestionDomain(
                                    answers,
                                    mapOfCorrectAnswers,
                                    binding.questionEditText.text.toString(),
                                    getQuestionType().type,
                                    binding.pointTextView.text.toString().toInt()
                                ), "change" to false
                    )
                )
            }
        }

    }

    private fun hideFocusAndKeyboard(){
        binding.questionEditText.clearFocus()
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
        if (mapOfCorrectAnswers.isEmpty() && (getQuestionType() == QuestionType.MULTIPLE_CHOICE_ONE_ANSWER || getQuestionType() == QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS)) {
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

    private fun buildDialog() {
        dialogFragment = NewAnswerBottomSheetFragment(this, position = answersAdapter.itemCount)
        dialogFragment?.show(parentFragmentManager, "")
    }

    private fun buildDialogWithText(text: String, position: Int) {
        dialogFragment = NewAnswerBottomSheetFragment(this, text, position)
        dialogFragment?.show(parentFragmentManager, "")
    }

    private fun addNewAnswerToAdapter(answer: String) {
        answersAdapter.addAnswer(NewAnswer(answer))
    }

    override fun addAnswer(text: String, position: Int) {
        setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
        answers[position] = text
        addNewAnswerToAdapter(text)
        dialogFragment?.dismiss()
    }

    override fun updateAnswer(text: String, position: Int) {
        setFragmentResult(QUESTION_KEY, bundleOf("change" to true))
        answersAdapter.updateAnswer(text, position)
        dialogFragment?.dismiss()
    }

    override fun longClick(model: NewAnswer, position: Int) {
        if (mapOfCorrectAnswers.containsKey(position)) {
            mapOfCorrectAnswers.remove(position)
            answersAdapter.markAsIncorrect(position)
        } else {
            when (getQuestionType()) {
                QuestionType.MULTIPLE_CHOICE_ONE_ANSWER -> {
                    if (mapOfCorrectAnswers.isEmpty()) {
                        mapOfCorrectAnswers[position] = model.title
                        answersAdapter.markAnswerAsCorrect(position)
                    }
                }
                QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS -> {
                    mapOfCorrectAnswers[position] = model.title
                    answersAdapter.markAnswerAsCorrect(position)
                }
                QuestionType.OPEN -> Unit
            }
        }
    }

}