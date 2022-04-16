package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.presentation.model.TestModel
import com.edu.common.utils.DividerItemDecorator
import com.edu.test.R
import com.edu.test.databinding.ViewPagerItemTestBinding

class TestsPagerAdapter(
    private val allTests: TestsAdapter,
    private val passedTests: TestsAdapter,
    private val isUserAdmin: Boolean = false,
    private val deleteTestListener: TestsAdapterListener
) : RecyclerView.Adapter<TestsPagerAdapter.TestPageViewHolder>() {

    companion object {
        private const val ALL_TESTS = 0
        private const val PASSED = 1
        private const val PAGES_COUNT = 2
        private const val PAGES_COUNT_ADMIN = 1
    }

    private val holderMap: HashMap<Int, TestPageViewHolder> = hashMapOf()

    private val tabEmptyState = hashMapOf(ALL_TESTS to false, PASSED to false)

    inner class TestPageViewHolder(val binding: ViewPagerItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            if (tabEmptyState[position] == true) {
                showEmptyDataState()
            } else {
                hideEmptyDataState()
            }
            binding.viewPagerRv.adapter = when (position) {
                ALL_TESTS -> {
                    allTests
                }
                PASSED -> {
                    passedTests
                }
                else -> null
            }
            val itemTouchHelper =
                ItemTouchHelper(object : SwipeHelper(binding.root.context, binding.viewPagerRv) {
                    override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                        return listOf(
                            UnderlayButton(
                                binding.root.context,
                                R.drawable.ic_baseline_delete_24,
                                R.color.main_color_pink
                            ) {
                                deleteTestListener.deleteTestClick(allTests.currentList[it])
                            }
                        )
                    }

                })
            itemTouchHelper.attachToRecyclerView(binding.viewPagerRv)
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

    override fun getItemCount(): Int {
        return if (isUserAdmin) PAGES_COUNT_ADMIN else PAGES_COUNT
    }

    interface TestsAdapterListener {
        fun deleteTestClick(model: TestModel)
    }
}