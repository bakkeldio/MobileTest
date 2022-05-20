package com.edu.test.presentation.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.presentation.model.TestModel
import com.edu.common.utils.DividerItemDecorator
import com.edu.test.databinding.ViewPagerItemTestBinding

class TestsPagerAdapter(
    private val allTests: TestsAdapter,
    private val passedTests: PassedTestsAdapter,
    private val isUserAdmin: Boolean = false,
    private val listener: TestsAdapterListener,
    private val selectionListener: SelectionListener,
    private val bundle: Bundle? = null
) : RecyclerView.Adapter<TestsPagerAdapter.TestPageViewHolder>() {

    companion object {
        private const val ALL_TESTS = 0
        private const val PASSED = 1
        private const val PAGES_COUNT = 2
        private const val PAGES_COUNT_ADMIN = 1
        private const val selectionId = "selectionId"
    }

    private val holderMap: HashMap<Int, TestPageViewHolder> = hashMapOf()

    private val tabEmptyState = hashMapOf(ALL_TESTS to false, PASSED to false)

    private lateinit var testsTracker: SelectionTracker<String>

    inner class TestPageViewHolder(val binding: ViewPagerItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            if (tabEmptyState[position] == true) {
                showEmptyDataState()
            } else {
                hideEmptyDataState()
            }
            binding.viewPagerRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (isUserAdmin) {
                        when (newState) {
                            RecyclerView.SCROLL_STATE_IDLE -> listener.showFloatingActionButton()
                            RecyclerView.SCROLL_STATE_DRAGGING -> listener.hideFloatingActionButton()
                        }
                    }
                }
            })
            binding.viewPagerRv.adapter = when (position) {
                ALL_TESTS -> {
                    allTests
                }
                PASSED -> {
                    passedTests
                }
                else -> null
            }

            if (position == ALL_TESTS) {
                testsTracker = buildSelectionTracker(binding, allTests)
                allTests.tracker = testsTracker
            }

            testsTracker.onRestoreInstanceState(bundle)

            testsTracker.addObserver(object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    selectionListener.getTestsSelection(testsTracker.selection.toList())
                }
            })

            binding.viewPagerRv.addItemDecoration(DividerItemDecorator(binding.root.context))
        }

        fun showEmptyDataState() {
            binding.groupNoData.visibility = View.VISIBLE
        }

        fun hideEmptyDataState() {
            binding.groupNoData.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestPageViewHolder {
        val viewHolder = TestPageViewHolder(
            ViewPagerItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holderMap[viewType] = viewHolder
        return viewHolder
    }

    override fun onBindViewHolder(holder: TestPageViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateEmptyState(position: Int, dataSize: Int) {
        tabEmptyState[position] = dataSize == 0
        if (dataSize == 0) {
            holderMap[position]?.showEmptyDataState()
        } else {
            holderMap[position]?.hideEmptyDataState()
        }
    }

    fun buildSelectionTracker(
        binding: ViewPagerItemTestBinding,
        adapter: TestsAdapter
    ) = SelectionTracker.Builder(
        //1
        selectionId,
        //2
        binding.viewPagerRv,
        //3
        TestItemKeyProvider(
            adapter
        ),
        TestsAdapter.TestsDetailsLookUp(binding.viewPagerRv),
        //4
        StorageStrategy.createStringStorage()
    ).withSelectionPredicate(
        //5
        SelectionPredicates.createSelectAnything()
    ).build()

    fun saveSelectionState(outState: Bundle) {
        testsTracker.onSaveInstanceState(outState)
    }

    fun getTestsSelection() = testsTracker.selection.map { it }


    override fun getItemCount(): Int {
        return if (isUserAdmin) PAGES_COUNT_ADMIN else PAGES_COUNT
    }

    interface TestsAdapterListener {
        fun showFloatingActionButton()
        fun hideFloatingActionButton()
    }

    interface SelectionListener {
        fun getTestsSelection(selectionList: List<String>)
    }
}