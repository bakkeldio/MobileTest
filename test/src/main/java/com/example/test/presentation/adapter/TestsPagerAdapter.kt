package com.example.test.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.common.utils.DividerItemDecorator
import com.example.test.databinding.ViewPagerItemTestBinding

class TestsPagerAdapter(
    private val testsToPass: TestsAdapter,
    private val passedTests: TestsAdapter
) : RecyclerView.Adapter<TestsPagerAdapter.TestPageViewHolder>() {

    companion object {
        private const val TO_PASS = 0
        private const val PASSED = 1
        private const val PAGES_COUNT = 2
    }

    private val holderMap: HashMap<Int, TestPageViewHolder> = hashMapOf()

    private val tabEmptyState = hashMapOf(TO_PASS to false, PASSED to false)

    inner class TestPageViewHolder(val binding: ViewPagerItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            if (tabEmptyState[position] == true) {
                showEmptyDataState()
            } else {
                hideEmptyDataState()
            }
            binding.viewPagerRv.adapter = when (position){
                TO_PASS -> {
                    testsToPass
                }
                PASSED -> {
                    passedTests
                }
                else -> null
            }
            binding.viewPagerRv.addItemDecoration(DividerItemDecorator(binding.root.context))
        }
        fun showEmptyDataState(){
            binding.groupNoData.visibility = View.VISIBLE
        }
        fun hideEmptyDataState(){
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

    fun updateEmptyState(position: Int, dataSize: Int){
       tabEmptyState[position] = dataSize == 0
        if (dataSize == 0){
            holderMap[position]?.showEmptyDataState()
        }else{
            holderMap[position]?.hideEmptyDataState()
        }
    }

    override fun getItemCount(): Int {
        return PAGES_COUNT
    }
}