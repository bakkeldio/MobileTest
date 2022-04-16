package com.edu.test.presentation.tests

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkInfo
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.model.TestModel
import com.edu.common.presentation.model.TestStatusEnum
import com.edu.common.utils.DebounceQueryTextListener
import com.edu.common.utils.buildMaterialAlertDialog
import com.edu.common.utils.showToast
import com.edu.test.R
import com.edu.test.databinding.FragmentTestsBinding
import com.edu.test.databinding.StartTestAlertDialogLayoutBinding
import com.edu.test.presentation.adapter.TestsAdapter
import com.edu.test.presentation.adapter.TestsAdapterTypeEnum
import com.edu.test.presentation.adapter.TestsPagerAdapter
import com.edu.test.presentation.model.UserTestsTypeEnum
import com.edu.test.presentation.question.QuestionsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class TestsFragment : BaseFragment<TestsViewModel, FragmentTestsBinding>(
    R.layout.fragment_tests,
    FragmentTestsBinding::bind
), TestsAdapter.ItemClickListener,
    TestsPagerAdapter.TestsAdapterListener {


    override val viewModel by viewModels<TestsViewModel>()

    companion object {
        private const val ALL_TESTS = 0
        private const val FINISHED_TESTS = 1
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val allTestsAdapter by lazy {
        TestsAdapter(this, TestsAdapterTypeEnum.ALL_TESTS, isTeacher)
    }

    private val passedTestsAdapter by lazy {
        TestsAdapter(this, TestsAdapterTypeEnum.PASSED_TESTS)
    }

    private val testPagerAdapter by lazy {
        TestsPagerAdapter(allTestsAdapter, passedTestsAdapter, isTeacher, this)
    }

    private val isTeacher by lazy {
        sharedPreferences.getBoolean("isUserAdmin", false)
    }

    private val args by navArgs<TestsFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isTeacher) {
            viewModel.getTeacherTests(args.groupId)
        } else {
            viewModel.getAllTests(args.groupId)
            viewModel.getCompletedTests(args.groupId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isTeacher) {
            binding.tabLayout.isVisible = false
        }

        binding.viewPager.adapter = testPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = resources.getString(UserTestsTypeEnum.getValueByPosition(position))
        }.attach()
        binding.toolbar.setupWithNavController(findNavController())
        val searchView = binding.toolbar.menu.findItem(R.id.action_search).actionView as SearchView

        binding.addTestBtn.isVisible = isTeacher

        searchView.setOnQueryTextListener(DebounceQueryTextListener {
            if (searchView.hasFocus()) {
                viewModel.searchThroughTests(
                    query = it,
                    groupId = args.groupId,
                    isUserAdmin = isTeacher,
                    searchCompletedTests = binding.viewPager.currentItem == FINISHED_TESTS
                )
            }
        })
        searchView.queryHint = resources.getString(R.string.search_hint_text)

        viewModel.apply {

            testsState.observe(viewLifecycleOwner) { data ->
                allTestsAdapter.submitList(data)
                testPagerAdapter.updateEmptyState(ALL_TESTS, data.size)
            }
            completedTestsState.observe(viewLifecycleOwner) { data ->
                passedTestsAdapter.submitList(data)
                testPagerAdapter.updateEmptyState(FINISHED_TESTS, data.size)
            }

            timerWorkResult.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    return@observe
                }
                val testId = it[0].progress.getString("testId")
                if (it[0].state == WorkInfo.State.RUNNING) {
                    updateCurrentTestStatusInList(TestStatusEnum.IN_PROGRESS, testId)
                }
            }

            uploadTestResult.observe(viewLifecycleOwner) {
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
            }
        }

        binding.addTestBtn.setOnClickListener {
            findNavController().navigate(
                TestsFragmentDirections.fromTestsFragmentToCreateQuestionsFragment(
                    args.groupId
                )
            )
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

    private fun buildCustomAlertDialog(model: TestModel, view: View? = null) {
        MaterialAlertDialogBuilder(requireContext()).setView(
            view
        )
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

    override fun onItemClick(model: TestModel) {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        val calendar3 = Calendar.getInstance()
        calendar1.time = model.date
        calendar3.time = model.date
        calendar3[Calendar.MINUTE] += model.time

        if (isTeacher) {
            //Open page with students who has passed the test
        } else {

            if (calendar1 > calendar2 && TestStatusEnum.PASSED != model.status){
                showToast("Время прохождения теста еще не наступило")
            }else{
                if (calendar2 < calendar3 || TestStatusEnum.PASSED == model.status){
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

                    }
                    else {
                        findNavController().navigate(
                            TestsFragmentDirections.fromTestsFragmentToTestResultFragment(
                                args.groupId,
                                model.uid
                            )
                        )
                    }
                }else{
                    showToast("Время прохождения теста прошло")
                }
            }
        }
    }

    override fun deleteTestClick(model: TestModel) {
        buildMaterialAlertDialog(
            description = resources.getString(R.string.confirm_test_removing, model.title),
            positiveBtnClick = {
                viewModel.deleteTest(args.groupId, model.uid)
            }).show()
    }

    override fun progressLoader(show: Boolean) {
        binding.progressBar.root.isVisible = show
    }
}