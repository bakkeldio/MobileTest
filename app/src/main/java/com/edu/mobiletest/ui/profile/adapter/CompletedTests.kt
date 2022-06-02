package com.edu.mobiletest.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.domain.model.TestDomainModel
import com.edu.mobiletest.databinding.ItemCompletedTestBinding
import com.edu.test.domain.model.PassedTestDomain

class CompletedTests(private val completedTest: (PassedTestDomain) -> Unit) :
    ListAdapter<PassedTestDomain, CompletedTests.TestVH>(object : DiffUtil.ItemCallback<PassedTestDomain>(){
        override fun areItemsTheSame(
            oldItem: PassedTestDomain,
            newItem: PassedTestDomain
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: PassedTestDomain,
            newItem: PassedTestDomain
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    inner class TestVH(private val binding: ItemCompletedTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            with(getItem(position)){
                if (position > 0){
                    binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        setMargins(15,0,0,0)
                    }
                }
                binding.testName.text = testTitle
                binding.root.setOnClickListener {
                    completedTest(this)
                }
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
}