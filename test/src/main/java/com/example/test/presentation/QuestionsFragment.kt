package com.example.test.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.work.WorkInfo
import com.example.common.data.test.TestProcessHandler
import com.example.common.data.test.model.QuestionResult
import com.example.common.domain.test.model.QuestionType
import com.example.common.presentation.ResourceState
import com.example.common.utils.ViewPager2ViewHeightAnimator
import com.example.common.utils.buildMaterialAlertDialog
import com.example.common.utils.showToast
import com.example.test.R
import com.example.test.databinding.FragmentQuestionsBinding
import com.example.test.presentation.adapter.QuestionsPagerAdapter
import com.example.test.presentation.adapter.TestPagesAdapter
import com.example.test.presentation.model.PageModel
import com.example.test.utils.SpanCountCalculator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionsFragment : Fragment(), TestPagesAdapter.PageListener {

    private val viewModel by viewModels<QuestionsViewModel>()

    private var _binding: FragmentQuestionsBinding? = null

    private val binding get() = _binding!!

    private val pagerAdapter by lazy {
        QuestionsPagerAdapter(this)
    }

    private val pagesAdapter by lazy {
        TestPagesAdapter(this)
    }

    private var questionsSize = 0

    private val navArgs by navArgs<QuestionsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.startTestTimerWork(navArgs.groupId, navArgs.testId, navArgs.testTitle, 1)
            .observe(viewLifecycleOwner, { listOfWorkInfos ->

                if (listOfWorkInfos.isNullOrEmpty()) {
                    return@observe
                }
                val time = listOfWorkInfos[0].progress.getInt("time", 1)
                binding.timeRemaining.text = resources.getString(R.string.minutes_left)
                    .format(resources.getQuantityString(R.plurals.minutes_plural, time, time))
                if (listOfWorkInfos[0].state == WorkInfo.State.SUCCEEDED) {
                    findNavController().popBackStack()
                }
            })
        childFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            when (QuestionType.getByValue(bundle.getString("questionType"))) {
                QuestionType.MULTIPLE_CHOICE_ONE_ANSWER -> {
                    TestProcessHandler.put(
                        QuestionResult(
                            bundle.getString("questionId", ""),
                            listOf(bundle.getString("answer", "")),
                            QuestionType.MULTIPLE_CHOICE_ONE_ANSWER
                        )
                    )
                    pagesAdapter.updateItem(
                        bundle.getInt("page"),
                        true,
                        QuestionType.MULTIPLE_CHOICE_ONE_ANSWER
                    )
                }
                QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS -> {
                    TestProcessHandler.put(
                        QuestionResult(
                            bundle.getString("questionId", ""),
                            bundle.getStringArrayList("checkedAnswers")?.toList() ?: emptyList(),
                            QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS
                        )
                    )
                    pagesAdapter.updateItem(
                        bundle.getInt("page"),
                        bundle.getBoolean("toCheck"),
                        QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS
                    )
                }
                QuestionType.OPEN -> {

                }
            }
        }
        binding.toolbar.setupWithNavController(findNavController())
        binding.viewPager.adapter = pagerAdapter

        binding.pagesRV.apply {
            val layoutManager = GridLayoutManager(
                requireContext(),
                SpanCountCalculator.calculateNumOfColumns(requireContext(), 65f)
            )
            setLayoutManager(layoutManager)
            adapter = pagesAdapter
        }
        ViewPager2ViewHeightAnimator().viewPager2 = binding.viewPager
        viewModel.getAllQuestions(navArgs.groupId, navArgs.testId)
        viewModel.questionsState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is ResourceState.Success -> {
                    TestProcessHandler.setQuestions(state.data)
                    pagesAdapter.submitData(state.data.map {
                        PageModel()
                    })
                    questionsSize = state.data.size
                    pagerAdapter.submitData(state.data)
                    binding.nextBtn.isVisible = true
                }
                is ResourceState.Error -> {
                    showToast(state.message)
                }
                else -> Unit
            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.prevBtn.isVisible = position > 0
                if (questionsSize - 1 == position) {
                    binding.nextBtn.text = resources.getString(R.string.finish)
                } else {
                    binding.nextBtn.text = resources.getString(R.string.next)
                }
            }
        })
        binding.nextBtn.setOnClickListener {
            if (binding.viewPager.currentItem == questionsSize - 1) {
                buildMaterialAlertDialog(
                    resources.getString(R.string.finish_test_and_submit_the_results),
                    positiveBtnClick = {
                        viewModel.submitResultAndCancelWorker(navArgs.groupId, navArgs.testId, navArgs.testTitle)
                        findNavController().navigate(
                            QuestionsFragmentDirections.actionFromQuestionsListToTestsList(
                                navArgs.groupId
                            )
                        )
                    }).show()
            } else {
                binding.viewPager.currentItem = binding.viewPager.currentItem + 1
            }
        }
        binding.prevBtn.setOnClickListener {
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPageClick(position: Int) {
        binding.viewPager.currentItem = position
    }
}