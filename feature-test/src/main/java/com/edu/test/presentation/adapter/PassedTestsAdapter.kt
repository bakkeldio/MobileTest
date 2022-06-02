package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.utils.getDateAndTime
import com.edu.test.databinding.ItemTestBinding
import com.edu.test.domain.model.PassedTestDomain

class PassedTestsAdapter(private val listener: Listener) :
    ListAdapter<PassedTestDomain, PassedTestsAdapter.PassedTestVH>(object :
        DiffUtil.ItemCallback<PassedTestDomain>() {
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

    inner class PassedTestVH(private val binding: ItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.statusTextView.isVisible = false
                binding.testTitle.text = testTitle

                binding.dateTime.text = testDate?.getDateAndTime()

                binding.root.setOnClickListener {
                    listener.onPassedTestClick(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassedTestVH {
        return PassedTestVH(
            ItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PassedTestVH, position: Int) {
        holder.bind(position)
    }

    interface Listener {
        fun onPassedTestClick(model: PassedTestDomain)
    }
}