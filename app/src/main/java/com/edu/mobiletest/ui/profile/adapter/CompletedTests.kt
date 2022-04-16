package com.edu.mobiletest.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.domain.model.TestDomainModel
import com.edu.mobiletest.databinding.ItemCompletedTestBinding

class CompletedTests(private val completedTest: (TestDomainModel) -> Unit) :
    RecyclerView.Adapter<CompletedTests.TestVH>() {

    private val tests: MutableList<TestDomainModel> = mutableListOf()

    fun sendTests(completedTests: List<TestDomainModel>) {
        tests.clear()
        tests.addAll(completedTests)
    }

    inner class TestVH(private val binding: ItemCompletedTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.testName.text = tests[position].title
            binding.root.setOnClickListener {
                completedTest(tests[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestVH {
        return TestVH(
            ItemCompletedTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TestVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return tests.size
    }
}