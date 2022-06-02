package com.edu.test.presentation.createTest

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.core.view.isVisible
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.ResourceState
import com.edu.common.utils.buildMaterialAlertDialog
import com.edu.common.utils.isAfterCurrentDate
import com.edu.common.utils.showToast
import com.edu.test.R
import com.edu.test.databinding.FragmentCreateQuestionsBinding
import com.edu.test.domain.model.CreateQuestionDomain
import com.edu.test.domain.model.dbModels.QuestionDomain
import com.edu.test.presentation.adapter.CreateQuestionsAdapter
import com.edu.test.presentation.adapter.QuestionsIndicatorAdapter
import com.edu.test.presentation.model.IndicatorItem
import com.edu.test.presentation.newQuestion.NewQuestionFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class CreateQuestionsFragment : BaseFragment<CreateTestViewModel, FragmentCreateQuestionsBinding>(
    R.layout.fragment_create_questions,
    FragmentCreateQuestionsBinding::bind
), QuestionsIndicatorAdapter.QuestionsAdapterListener {

    override val viewModel by hiltNavGraphViewModels<CreateTestViewModel>(R.id.navigation_test)

    private val questionsIndicatorAdapter by lazy {
        QuestionsIndicatorAdapter(this)
    }

    private var questions: List<QuestionDomain> = listOf()
    private val args by navArgs<CreateQuestionsFragmentArgs>()

    private val pagerAdapter by lazy {
        CreateQuestionsAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getSavedTestQuestions(args.testUid)
    }

    private var oldPosition: Int = 0

    override fun createNewQuestion(position: Int) {
        if (questions.isEmpty() || questions[position - 1].title.isNotEmpty()) {
            viewModel.createQuestion(args.testUid)
        } else {
            showToast(resources.getString(R.string.save_your_previous_question))
        }
    }

    override fun moveToPage(position: Int) {
        if (!binding.viewPager.isUserInputEnabled && position != binding.viewPager.currentItem) {
            showToast(resources.getString(R.string.save_changes))
        } else {
            questionsIndicatorAdapter.updateIndicatorAt(binding.viewPager.currentItem, position)
            binding.viewPager.currentItem = position
        }
    }

    override fun setupUI() {
        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.upload_item -> {
                    if (questions.isNotEmpty() && questions.last().title.isNotEmpty()) {
                        val testDateAfterCurrentDate =
                            viewModel.savedTest.value?.date?.isAfterCurrentDate() ?: false
                        buildMaterialAlertDialog(
                            if (testDateAfterCurrentDate)
                                resources.getString(R.string.want_to_upload_test)
                            else resources.getString(
                                R.string.change_date_of_test
                            ),
                            positiveBtnClick = {
                                if (testDateAfterCurrentDate) {
                                    viewModel.uploadNewTest(args.groupId, args.testUid)
                                } else {
                                    findNavController().popBackStack()
                                }
                            }).show()
                    } else {
                        showToast(resources.getString(R.string.save_last_question))
                    }
                    true
                }
                else -> false
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                lifecycleScope.launchWhenCreated {
                    delay(100)
                    questionsIndicatorAdapter.updateIndicatorAt(oldPosition, position)
                }
                oldPosition = position
            }
        })
        childFragmentManager.setFragmentResultListener(
            NewQuestionFragment.QUESTION_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean("change")) {
                binding.viewPager.isUserInputEnabled = false
            } else {
                binding.viewPager.isUserInputEnabled = true
                if (bundle.getInt(NewQuestionFragment.QUESTION_KEY, 0) != 0) {
                    viewModel.deleteQuestion(bundle.getInt(NewQuestionFragment.QUESTION_KEY, 0))
                } else {
                    val question =
                        bundle.getParcelable<CreateQuestionDomain>(NewQuestionFragment.QUESTION_MODEL)!!
                    viewModel.updateQuestion(question, args.testUid)
                }
            }

        }
        binding.viewPager.adapter = pagerAdapter
        binding.recyclerView.adapter = questionsIndicatorAdapter
        val child = binding.viewPager.getChildAt(0)
        if (child is RecyclerView) {
            child.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    override fun setupVM() {
        super.setupVM()
        viewModel.createTestState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.root.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    showToast(resources.getString(R.string.test_uploaded_successfully))
                    findNavController().navigate(
                        CreateQuestionsFragmentDirections.actionFromCreateQuestionsFragmentToTestsFragment(
                            args.groupId
                        )
                    )
                }
                is ResourceState.Error -> {
                    showToast(state.message)
                }
                else -> Unit
            }
        }
        viewModel.questions.observe(viewLifecycleOwner) { questionsList ->
            var index = questionsIndicatorAdapter.currentList.indexOfFirst {
                it.isCurrentPage
            }
            val indicators = questionsList.mapIndexedTo(mutableListOf()) { ind, q ->
                IndicatorItem(q.id, false, ind + 1)
            }

            if (index != -1) {
                val question = questionsList.find {
                    it.id == questionsIndicatorAdapter.currentList[index].id
                }
                if (question == null) {
                    index -= 1
                }
                if (index == -1 && questionsList.isNotEmpty()) {
                    index = 0
                }
                if (indicators.isNotEmpty()) {
                    indicators[index] = indicators[index].copy(isCurrentPage = true)
                }
            }
            indicators.add(IndicatorItem(id = 0, position = indicators.size))
            pagerAdapter.setItems(questionsList)
            questionsIndicatorAdapter.submitList(indicators.toList())
            questions = questionsList

        }
    }
}