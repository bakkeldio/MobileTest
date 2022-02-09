package com.example.test.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.common.data.test.TestCreationHandler
import com.example.common.utils.showToast
import com.example.test.R
import com.example.test.databinding.FragmentCreateQuestionsBinding
import com.example.test.presentation.adapter.CreateQuestionsAdapter
import com.example.test.presentation.adapter.QuestionsIndicatorAdapter

class CreateQuestionsFragment : Fragment(), QuestionsIndicatorAdapter.QuestionsAdapterListener {

    private var _binding: FragmentCreateQuestionsBinding? = null
    private val binding: FragmentCreateQuestionsBinding get() = _binding!!

    private val questionsIndicatorAdapter by lazy {
        QuestionsIndicatorAdapter(this)
    }

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

    @SuppressLint("ClickableViewAccessibility")
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
                    ChooseDateTimeBottomSheet().show(requireActivity().supportFragmentManager, "")
                    true
                }
                else -> false
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