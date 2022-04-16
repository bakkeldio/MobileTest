package com.edu.test.presentation.question

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.edu.common.domain.model.QuestionDomain
import com.edu.common.domain.model.QuestionType
import com.edu.test.R
import com.edu.test.data.model.QuestionResult
import com.edu.test.databinding.FragmentQuestionBinding
import com.edu.test.presentation.adapter.AnswerItemKeyProvider
import com.edu.test.presentation.adapter.AnswersAdapter
import com.edu.test.presentation.model.ItemAnswer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private val adapter: AnswersAdapter by lazy {
        AnswersAdapter()
    }

    companion object {
        fun createInstance(bundle: Bundle): QuestionFragment {
            val instance = QuestionFragment()
            instance.arguments = bundle
            return instance
        }
    }

    private val questionModel by lazy {
        requireArguments().getParcelable<QuestionDomain>("question")
    }

    private val questionResult by lazy {
        requireArguments().getParcelable<QuestionResult>("questionResult")
    }

    private val questionType by lazy {
        questionModel?.questionType ?: QuestionType.MULTIPLE_CHOICE_ONE_ANSWER
    }

    private val position by lazy {
        requireArguments().getInt("position")
    }

    var job: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.questionTextView.text = "${position + 1}. ${questionModel?.question}"

        binding.answersRv.isVisible = questionType != QuestionType.OPEN
        binding.openQuestionEditText.isVisible = questionType == QuestionType.OPEN

        binding.openQuestionEditText.setOnTouchListener { v, event ->
            if (v.id == R.id.openQuestionEditText) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        if (questionType == QuestionType.OPEN) {
            binding.openQuestionEditText.setText(questionResult?.answerOpenQuestion)
            binding.openQuestionEditText.addTextChangedListener {
                it?.let { answer ->
                    job?.cancel()
                    job = lifecycleScope.launch {
                        delay(500)
                        setFragmentResult(
                            "result", bundleOf(
                                "page" to position,
                                "questionId" to questionModel?.uid,
                                "toCheck" to answer.isNotEmpty(),
                                "answer" to answer.toString(),
                                "questionType" to questionType.type
                            )
                        )
                    }

                }
            }
        } else {
            val answers = questionModel?.answersList?.map {
                ItemAnswer(
                    it.key,
                    it.value
                )
            }
            binding.answersRv.adapter = adapter
            val tracker = SelectionTracker.Builder(
                "mySelection",
                binding.answersRv,
                AnswerItemKeyProvider(),
                AnswersAdapter.MyItemsDetailsLookUp(binding.answersRv),
                StorageStrategy.createLongStorage()
            )
                .withSelectionPredicate(
                    if (questionModel?.questionType == QuestionType.MULTIPLE_CHOICE_ONE_ANSWER) SelectionPredicates.createSelectSingleAnything() else
                        SelectionPredicates.createSelectAnything()
                )
                .build()
            adapter.tracker = tracker
            adapter.submitList(answers)

            questionResult?.let { question ->
                question.userAnswer.forEach { answer ->
                    tracker.select(answer.toLong())
                }
            }
            tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val toCheck = tracker.selection.size() > 0
                    setFragmentResult(
                        "result", bundleOf(
                            "page" to position,
                            "questionId" to questionModel?.uid,
                            "checkedAnswers" to tracker.selection.map {
                                it.toInt()
                            },
                            "toCheck" to toCheck,
                            "questionType" to questionType.type
                        )
                    )
                }
            })
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}