package com.edu.mobiletest.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.domain.model.TestDomainModel
import com.edu.mobiletest.databinding.ItemCompletedTestBinding
import com.edu.test.domain.model.PassedTestDomain

class CompletedTests(private val completedTest: (PassedTestDomain) -> Unit) :
    RecyclerView.Adapter<CompletedTests.TestVH>() {

    private val tests: MutableList<PassedTestDomain> = mutableListOf()

    fun sendTests(completedTests: List<PassedTestDomain>) {
        tests.clear()
        tests.addAll(completedTests)
    }

    inner class TestVH(private val binding: ItemCompletedTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.testName.text = tests[position].testTitle
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