package com.edu.test.presentation.testResult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.edu.common.utils.addItemDecorationWithoutLastItem
import com.edu.common.utils.showToast
import com.edu.test.databinding.FragmentTestResultBinding
import com.edu.test.presentation.adapter.QuestionsResultAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestResultFragment : Fragment() {

    private var _binding: FragmentTestResultBinding? = null
    private val binding: FragmentTestResultBinding get() = _binding!!

    private val viewModel by viewModels<TestResultViewModel>()

    private val navArgs by navArgs<TestResultFragmentArgs>()

    private val adapter: QuestionsResultAdapter by lazy {
        QuestionsResultAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getResultsOfTest(navArgs.groupId, navArgs.testId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNavController(findNavController())
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progress.root.isVisible = it
        }
        viewModel.testResultError.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })

        binding.testResultRv.apply {
            addItemDecorationWithoutLastItem()
        }

        viewModel.testResult.observe(viewLifecycleOwner) { data ->
            binding.testResultRv.adapter = this.adapter
            adapter.submitList(data)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}