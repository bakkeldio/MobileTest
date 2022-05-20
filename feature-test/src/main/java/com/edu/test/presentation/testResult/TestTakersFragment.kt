package com.edu.test.presentation.testResult

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.edu.common.presentation.BaseFragment
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.test.R
import com.edu.test.databinding.FragmentTestTakersBinding
import com.edu.test.domain.model.TestResultDomain
import com.edu.test.presentation.adapter.TestTakersAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestTakersFragment : BaseFragment<TestTakersViewModel, FragmentTestTakersBinding>(
    R.layout.fragment_test_takers,
    FragmentTestTakersBinding::bind
), TestTakersAdapter.Listener {

    @Inject
    lateinit var imageLoader: IImageLoader

    override val viewModel: TestTakersViewModel by viewModels()

    private val adapter: TestTakersAdapter by lazy {
        TestTakersAdapter(imageLoader, this)
    }

    private val args by navArgs<TestTakersFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getStudents(args.testId)
    }

    override fun setupUI() {
        binding.toolbar.setupWithNavController(findNavController())
        binding.recyclerView.adapter = adapter
    }

    override fun setupVM() {
        super.setupVM()
        viewModel.studentsWhoPassedTestLiveData.observe(viewLifecycleOwner) { list ->
            binding.studentLogo.isVisible = list.isEmpty()
            binding.msg.isVisible = list.isEmpty()
            adapter.submitList(list)
        }
    }


    override fun progressLoader(show: Boolean) {
        binding.loader.root.isVisible = show
    }

    override fun onStudentResultClick(model: TestResultDomain) {
        findNavController().navigate(
            TestTakersFragmentDirections.fromTestTakersFragmentToTestResultFragment(
                args.groupId,
                args.testId,
                model.studentUid
            )
        )
    }


}