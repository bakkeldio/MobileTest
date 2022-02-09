package com.example.test.presentation

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkInfo
import com.example.common.presentation.model.TestModel
import com.example.common.presentation.model.TestStatusEnum
import com.example.common.utils.DebounceQueryTextListener
import com.example.common.utils.showToast
import com.example.test.R
import com.example.test.databinding.FragmentTestsBinding
import com.example.test.databinding.StartTestAlertDialogLayoutBinding
import com.example.test.presentation.adapter.TestsAdapter
import com.example.test.presentation.adapter.TestsAdapterTypeEnum
import com.example.test.presentation.adapter.TestsPagerAdapter
import com.example.test.presentation.model.UserTestsTypeEnum
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
internal class TestsFragment : Fragment(), TestsAdapter.ItemClickListener {


    private val viewModel by viewModels<TestsViewModel>()
    private var _binding: FragmentTestsBinding? = null

    private val binding get() = _binding!!

    companion object {
        private const val ALL_TESTS = 0
        private const val FINISHED_TESTS = 1
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val allTestsAdapter by lazy {
        TestsAdapter(this, TestsAdapterTypeEnum.ALL_TESTS)
    }

    private val passedTestsAdapter by lazy {
        TestsAdapter(this, TestsAdapterTypeEnum.PASSED_TESTS)
    }

    private val testPagerAdapter by lazy {
        TestsPagerAdapter(allTestsAdapter, passedTestsAdapter)
    }

    private val args by navArgs<TestsFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllTests(args.groupId)
        viewModel.getCompletedTests(args.groupId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentTestsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setupWithNavController(findNavController())
        val searchView = binding.toolbar.menu.findItem(R.id.action_search).actionView as SearchView

        binding.viewPager.adapter = testPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = resources.getString(UserTestsTypeEnum.getValueByPosition(position))
        }.attach()
        binding.addTestBtn.isVisible = sharedPreferences.getBoolean("isUserAdmin", false)

        searchView.setOnQueryTextListener(DebounceQueryTextListener {
            if (searchView.hasFocus()) {
                viewModel.searchThroughTests(query = it, groupId = args.groupId)
            }
        })
        searchView.queryHint = resources.getString(R.string.search_hint_text)

        viewModel.apply {

            error.observe(viewLifecycleOwner, {
                showToast(it)
            })
            loading.observe(viewLifecycleOwner, {
                binding.progressBar.root.isVisible = it
            })
            testsState.observe(viewLifecycleOwner, { data ->
                allTestsAdapter.submitList(data)
                testPagerAdapter.updateEmptyState(0, allTestsAdapter.itemCount)

            })
            completedTestsState.observe(viewLifecycleOwner, { data ->
                passedTestsAdapter.submitList(data)
                testPagerAdapter.updateEmptyState(1, passedTestsAdapter.itemCount)
            })

            timerWorkResult.observe(viewLifecycleOwner, {
                if (it.isNullOrEmpty()) {
                    return@observe
                }
                val testId = it[0].progress.getString("testId")
                if (it[0].state == WorkInfo.State.RUNNING) {
                    updateCurrentTestStatusInList(TestStatusEnum.IN_PROGRESS, testId)
                }
            })

            uploadTestResult.observe(viewLifecycleOwner, {
                if (it.isNullOrEmpty()) {
                    return@observe
                }
                if (it[0].state == WorkInfo.State.SUCCEEDED) {
                    val testId = it[0].outputData.getString(QuestionsViewModel.TEST_ID)
                    updateCurrentTestStatusInList(TestStatusEnum.PASSED, testId)
                }
                if (it[0].state.isFinished) {
                    viewModel.pruneWorks()
                }
            })
        }

        binding.addTestBtn.setOnClickListener {
            findNavController().navigate(TestsFragmentDirections.fromTestsFragmentToCreateQuestionsFragment())
        }

    }

    private fun updateCurrentTestStatusInList(status: TestStatusEnum, testId: String?) {
        val arrayList = ArrayList(allTestsAdapter.currentList)
        val model = arrayList.find {
            it.uid == testId
        }

        if (arrayList.indexOf(model) != -1) {
            arrayList[arrayList.indexOf(model)] =
                model?.copy(status = status)
            sendUpdatedTestDataToLiveData(arrayList)
        }

    }

    private fun sendUpdatedTestDataToLiveData(tests: List<TestModel>) {
        viewModel.updateTestsLiveData(tests)
    }

    private fun buildCustomAlertDialog(model: TestModel, view: View) {
        MaterialAlertDialogBuilder(requireContext()).setView(
            view
        ).setTitle(model.title)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.ok_text)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigate(
                    TestsFragmentDirections.fromTestsFragmentToTestQuestionsFragment(
                        args.groupId,
                        model.uid,
                        model.title ?: ""
                    )
                )
            }.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(model: TestModel) {
        if (binding.viewPager.currentItem == ALL_TESTS) {

            when (model.status) {
                TestStatusEnum.PASSED -> {
                    findNavController().navigate(
                        TestsFragmentDirections.fromTestsFragmentToTestResultFragment(
                            args.groupId,
                            model.uid
                        )
                    )
                }
                TestStatusEnum.IN_PROGRESS -> {
                    findNavController().navigate(
                        TestsFragmentDirections.fromTestsFragmentToTestQuestionsFragment(
                            args.groupId,
                            model.uid,
                            model.title ?: ""
                        )
                    )
                }
                TestStatusEnum.NOT_STARTED -> {
                    val binding = StartTestAlertDialogLayoutBinding.inflate(
                        LayoutInflater.from(requireContext()), null, false
                    )
                    val time = resources.getQuantityString(
                        R.plurals.minutes_plural,
                        model.time,
                        model.time
                    )
                    binding.alertMessage.text =
                        resources.getString(R.string.alert_message_before_test).format(time)
                    buildCustomAlertDialog(model, binding.root)
                }
            }

        } else {

            findNavController().navigate(R.id.newQuestionFragment)
            /*
            findNavController().navigate(
                TestsFragmentDirections.fromTestsFragmentToTestResultFragment(
                    args.groupId,
                    model.uid
                )
            )
             */
        }
    }
}