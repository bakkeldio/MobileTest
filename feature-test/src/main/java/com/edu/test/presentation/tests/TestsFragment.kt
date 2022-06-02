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
import com.edu.test.domain.model.PassedTestDomain
import com.edu.test.domain.model.dbModels.TestDomain
import com.edu.test.presentation.adapter.PassedTestsAdapter
import com.edu.test.presentation.adapter.TestsAdapter
import com.edu.test.presentation.adapter.TestsPagerAdapter
import com.edu.test.presentation.adapter.UnPublishedTestsAdapter
import com.edu.test.presentation.model.TeacherTestsTypeEnum
import com.edu.test.presentation.model.UserTestsTypeEnum
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
    TestsPagerAdapter.TestsAdapterListener, PassedTestsAdapter.Listener,
    TestsPagerAdapter.SelectionListener, UnPublishedTestsAdapter.Listener {


    override val viewModel by viewModels<TestsViewModel>()

    companion object {
        private const val ALL_TESTS = 0
        private const val FINISHED_TESTS = 1
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val allTestsAdapter by lazy {
        TestsAdapter(this, isTeacher)
    }

    private val passedTestsAdapter by lazy {
        PassedTestsAdapter(this)
    }

    private val unPublishedTestsAdapter by lazy {
        UnPublishedTestsAdapter(this)
    }

    private val testPagerAdapter by lazy {
        TestsPagerAdapter(
            allTestsAdapter,
            passedTestsAdapter,
            unPublishedTestsAdapter,
            isTeacher,
            this,
            this
        )
    }

    private val isTeacher by lazy {
        sharedPreferences.getBoolean("isUserAdmin", false)
    }

    private val args by navArgs<TestsFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isTeacher) {
            viewModel.getTeacherTests(args.groupId)
            viewModel.getUnpublishedTests(args.groupId)
        } else {
            viewModel.getAllTests(args.groupId)
        }
    }

    override fun setupUI() {

        binding.viewPager.adapter = testPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = resources.getString(
                if (isTeacher) TeacherTestsTypeEnum.getValueByPosition(position)
                else
                    UserTestsTypeEnum.getValueByPosition(
                        position
                    )
            )
        }.attach()
        binding.toolbar.setupWithNavController(findNavController())
        val searchView = binding.toolbar.menu.findItem(R.id.action_search).actionView as SearchView

        binding.floatingActionButton.isVisible = isTeacher

        searchView.setOnQueryTextListener(DebounceQueryTextListener {
            if (searchView.hasFocus()) {
                if (binding.tabLayout.selectedTabPosition == FINISHED_TESTS) {
                    viewModel.searchThroughCompletedTests(args.groupId, it)
                } else {
                    viewModel.searchThroughTests(args.groupId, it, isTeacher)
                }
            }
        })
        searchView.queryHint = resources.getString(R.string.search_hint_text)

        binding.floatingActionButton.setOnClickListener {
            if (testPagerAdapter.getTestsSelection()
                    .isNotEmpty() || testPagerAdapter.getUnPublishedTestsSelection().isNotEmpty()
            ) {
                buildMaterialAlertDialog(
                    description = resources.getString(R.string.confirm_test_removing),
                    positiveBtnClick = {
                        if (binding.viewPager.currentItem == ALL_TESTS) {
                            viewModel.deleteTest(args.groupId, testPagerAdapter.getTestsSelection())
                        } else {
                            viewModel.deleteTestsFromDB(testPagerAdapter.getUnPublishedTestsSelection())
                        }
                    }).show()
            } else {
                findNavController().navigate(
                    TestsFragmentDirections.fromTestsFragmentToCreateTestFragment(
                        args.groupId
                    )
                )
            }
        }
    }

    override fun setupVM() {
        super.setupVM()
        viewModel.apply {

            testsState.observe(viewLifecycleOwner) { data ->
                allTestsAdapter.submitList(data)
                testPagerAdapter.updateEmptyState(ALL_TESTS, data.size)
            }
            completedTestsState.observe(viewLifecycleOwner) { data ->
                passedTestsAdapter.submitList(data)
                testPagerAdapter.updateEmptyState(FINISHED_TESTS, data.size)
            }

            unPublishedTests.observe(viewLifecycleOwner) { data ->
                unPublishedTestsAdapter.submitList(data)
            }

            timerWorkResult.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    return@observe
                }
                val testId = it[0].progress.getString("testId")
                if (it[0].state == WorkInfo.State.RUNNING) {
                    makeCurrentTestStatusInProgress(testId)
                }
            }

            uploadTestResult.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    return@observe
                }

                if (it[0].state.isFinished) {
                    viewModel.pruneWorks()
                }
            }
        }
    }

    private fun makeCurrentTestStatusInProgress(testId: String?) {
        val arrayList = ArrayList(viewModel.testsState.value ?: emptyList())
        val model = arrayList.find {
            it.uid == testId
        }

        if (arrayList.indexOf(model) != -1) {

            arrayList[arrayList.indexOf(model)] =
                model?.copy(status = TestStatusEnum.IN_PROGRESS)

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
                        model.title ?: "", model.time
                    )
                )
            }.show()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        testPagerAdapter.saveSelectionState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun showFloatingActionButton() {
        binding.floatingActionButton.show()
    }

    override fun hideFloatingActionButton() {
        binding.floatingActionButton.hide()
    }

    override fun progressLoader(show: Boolean) {
        binding.progressBar.root.isVisible = show
    }

    override fun onTestClick(model: TestModel) {
        if (!isTeacher) {
            val calendar1 = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            val calendar3 = Calendar.getInstance()
            calendar1.time = model.date
            calendar3.time = model.date
            calendar3[Calendar.MINUTE] += model.time

            if (calendar1 > calendar2) {
                showToast(resources.getString(R.string.time_to_pass_the_test_has_not_come_yet))
            } else {
                if (calendar3 > calendar2 || model.status == TestStatusEnum.PASSED) {
                    when (model.status) {
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
                                resources.getString(R.string.alert_message_before_test)
                                    .format(time)
                            buildCustomAlertDialog(model, binding.root)

                        }
                        TestStatusEnum.PASSED -> {
                            findNavController().navigate(
                                TestsFragmentDirections.fromTestsFragmentToTestResultFragment(
                                    args.groupId,
                                    model.uid
                                )
                            )
                        }
                    }
                } else {
                    showToast(resources.getString(R.string.time_to_pass_the_test_has_passed))
                }
            }
        } else {
            findNavController().navigate(
                TestsFragmentDirections.fromTestsFragmentToTestTakersFragment(
                    model.title,
                    model.uid,
                    args.groupId
                )
            )
        }
    }

    override fun onPassedTestClick(model: PassedTestDomain) {
        findNavController().navigate(
            TestsFragmentDirections.fromTestsFragmentToTestResultFragment(
                args.groupId,
                model.uid
            )
        )
    }

    override fun getTestsSelection(selectionList: List<String>) {
        showHideRemoveButton(selectionList.size)
    }

    override fun getUnPublishedTestsSelection(selectionList: List<String>) {
        showHideRemoveButton(selectionList.size)
    }

    private fun showHideRemoveButton(selectionSize: Int) {
        if (selectionSize > 0) {
            binding.floatingActionButton.setImageResource(R.drawable.ic_remove)
        } else {
            binding.floatingActionButton.setImageResource(R.drawable.icon_add)
        }
    }

    override fun onTestClick(test: TestDomain) {
        findNavController().navigate(
            TestsFragmentDirections.fromTestsFragmentToCreateTestFragment(
                args.groupId,
                test.uid
            )
        )
    }
}