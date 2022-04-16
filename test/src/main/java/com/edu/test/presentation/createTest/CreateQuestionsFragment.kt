package com.edu.test.presentation.createTest

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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.edu.common.presentation.ResourceState
import com.edu.common.utils.showToast
import com.edu.test.R
import com.edu.test.data.datamanager.TestCreationHandler
import com.edu.test.databinding.FragmentCreateQuestionsBinding
import com.edu.test.presentation.adapter.CreateQuestionsAdapter
import com.edu.test.presentation.adapter.QuestionsIndicatorAdapter
import com.edu.test.presentation.newQuestion.NewQuestionFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateQuestionsFragment : Fragment(), QuestionsIndicatorAdapter.QuestionsAdapterListener {

    private var _binding: FragmentCreateQuestionsBinding? = null
    private val binding: FragmentCreateQuestionsBinding get() = _binding!!

    private val viewModel by viewModels<CreateTestViewModel>()

    private val questionsIndicatorAdapter by lazy {
        QuestionsIndicatorAdapter(this)
    }

    private val args by navArgs<CreateQuestionsFragmentArgs>()
    private val pagerAdapter by lazy {
        CreateQuestionsAdapter(this)
    }

    private var oldPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.remove_item -> {
                    TestCreationHandler.removeQuestion(binding.viewPager.currentItem)
                    questionsIndicatorAdapter.removeIndicatorAtPosition(binding.viewPager.currentItem)
                    pagerAdapter.removeNewPage(binding.viewPager.currentItem)
                    true
                }
                R.id.upload_item -> {
                    if (TestCreationHandler.checkIfLastQuestionWasSaved(questionsIndicatorAdapter.itemCount)) {
                        ChooseDateTimeBottomSheet { dialog ->
                            viewModel.createNewTest(args.groupId)
                            dialog.dismiss()
                        }.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    } else {
                        showToast(resources.getString(R.string.save_last_question))
                    }
                    true
                }
                else -> false
            }
        }

        viewModel.createTestState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.root.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    showToast("Тест успешно загружен")
                    findNavController().popBackStack()
                }
                is ResourceState.Error -> {
                    showToast(state.message)
                }
                else -> Unit
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                questionsIndicatorAdapter.updateIndicatorAt(oldPosition, position)
                oldPosition = position
            }
        })
        childFragmentManager.setFragmentResultListener(
            NewQuestionFragment.QUESTION_KEY,
            this
        ) { _, bundle ->
            if (bundle.getBoolean("change")) {
                binding.viewPager.isUserInputEnabled = false
            } else {
                binding.viewPager.isUserInputEnabled = true
                TestCreationHandler.createUpdateQuestion(
                    binding.viewPager.currentItem,
                    bundle.getParcelable(NewQuestionFragment.QUESTION_MODEL)!!
                )
            }

        }
        binding.viewPager.adapter = pagerAdapter
        binding.recyclerView.adapter = questionsIndicatorAdapter
        val child = binding.viewPager.getChildAt(0)
        if (child is RecyclerView) {
            child.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    override fun createNewQuestion(position: Int) {
        if (position != 0) {
            if (TestCreationHandler.hasQuestionWithPosition(position - 1)) {
                pagerAdapter.createNewPage(position)
                questionsIndicatorAdapter.addIndicatorAtPosition(position)
                binding.viewPager.currentItem = position
            } else {
                showToast(resources.getString(R.string.save_your_previous_question))
            }
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

    override fun onDestroy() {
        super.onDestroy()
        TestCreationHandler.clear()
    }
}