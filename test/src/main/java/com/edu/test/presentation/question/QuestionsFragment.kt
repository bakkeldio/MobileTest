package com.edu.test.presentation.question

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.work.WorkInfo
import com.edu.common.domain.model.QuestionType
import com.edu.common.presentation.BaseFragment
import com.edu.common.utils.SpanCountCalculator
import com.edu.common.utils.ViewPager2ViewHeightAnimator
import com.edu.common.utils.buildMaterialAlertDialog
import com.edu.common.utils.removeOverScroll
import com.edu.test.R
import com.edu.test.data.datamanager.TestProcessHandler
import com.edu.test.data.model.QuestionResult
import com.edu.test.databinding.FragmentQuestionsBinding
import com.edu.test.presentation.adapter.QuestionsPagerAdapter
import com.edu.test.presentation.adapter.TestPagesAdapter
import com.edu.test.presentation.model.PageModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionsFragment : BaseFragment<QuestionsViewModel, FragmentQuestionsBinding>(
    R.layout.fragment_questions,
    FragmentQuestionsBinding::bind
),
    TestPagesAdapter.PageListener {

    override val viewModel by viewModels<QuestionsViewModel>()

    private val pagerAdapter by lazy {
        QuestionsPagerAdapter(this)
    }

    private val pagesAdapter by lazy {
        TestPagesAdapter(this)
    }

    private var questionsSize = 0

    private val navArgs by navArgs<QuestionsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.startTestTimerWork(navArgs.groupId, navArgs.testId, navArgs.testTitle, 10)
        viewModel.getAllQuestions(navArgs.groupId, navArgs.testId)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.timerLiveData.observe(viewLifecycleOwner) { workInfos ->
            if (workInfos != null) {
                val time = workInfos[0].progress.getInt("time", 1)
                binding.timeRemaining.text = resources.getString(R.string.minutes_left)
                    .format(resources.getQuantityString(R.plurals.minutes_plural, time, time))
                if (workInfos[0].state == WorkInfo.State.SUCCEEDED) {
                    findNavController().popBackStack()
                }
            }
        }


        childFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            if (QuestionType.getByValue(bundle.getString("questionType")) == QuestionType.OPEN) {
                TestProcessHandler.put(
                    QuestionResult(
                        bundle.getString("questionId", ""),
                        emptyList(),
                        QuestionType.OPEN,
                        bundle.getString("answer")
                    )
                )
            } else {
                TestProcessHandler.put(
                    QuestionResult(
                        bundle.getString("questionId", ""),
                        bundle.getIntegerArrayList("checkedAnswers")?.toList() ?: emptyList(),
                        QuestionType.getByValue(bundle.getString("questionType"))
                    )
                )
            }
            pagesAdapter.updateItem(
                bundle.getInt("page"),
                bundle.getBoolean("toCheck")
            )

        }
        TestProcessHandler.getUserAnswers().forEach {

        }
        binding.toolbar.setupWithNavController(findNavController())
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.removeOverScroll()
        binding.pagesRV.apply {
            val layoutManager = GridLayoutManager(
                requireContext(),
                SpanCountCalculator.calculateNumOfColumns(requireContext(), 65f)
            )
            setLayoutManager(layoutManager)
            adapter = pagesAdapter
        }
        ViewPager2ViewHeightAnimator().viewPager2 = binding.viewPager
        viewModel.questionsState.observe(viewLifecycleOwner) { questions ->
            TestProcessHandler.setQuestions(questions)
            pagesAdapter.submitData(questions.map {
                PageModel(
                    !TestProcessHandler.userAnswersMap[it.uid]?.userAnswer.isNullOrEmpty()
                            || !TestProcessHandler.userAnswersMap[it.uid]?.answerOpenQuestion.isNullOrEmpty()
                )
            })
            questionsSize = questions.size
            pagerAdapter.submitData(questions)
            binding.nextBtn.isVisible = true
        }



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
                        viewModel.submitResultAndCancelWorker(
                            navArgs.groupId,
                            navArgs.testId,
                            navArgs.testTitle
                        )
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

    override fun onPageClick(position: Int) {
        binding.viewPager.currentItem = position
    }

    override fun progressLoader(show: Boolean) {
        binding.progress.root.isVisible = show
    }
}