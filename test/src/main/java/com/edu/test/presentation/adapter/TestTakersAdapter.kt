package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.test.R
import com.edu.test.databinding.ItemPersonResultBinding
import com.edu.test.domain.model.TestResultDomain

class TestTakersAdapter(private val imageLoader: IImageLoader, private val listener: Listener) :
    ListAdapter<TestResultDomain, TestTakersAdapter.TestTakerVH>(object :
        DiffUtil.ItemCallback<TestResultDomain>() {
        override fun areItemsTheSame(
            oldItem: TestResultDomain,
            newItem: TestResultDomain
        ): Boolean {
            return oldItem.studentUid == newItem.studentUid
        }

        override fun areContentsTheSame(
            oldItem: TestResultDomain,
            newItem: TestResultDomain
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    inner class TestTakerVH(private val binding: ItemPersonResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                imageLoader.loadImageWithCircleShape(
                    studentAvatar,
                    binding.avatar,
                    R.drawable.ic_chat_item
                )

                binding.name.text = studentName
                binding.seeResultsBtn.setOnClickListener {
                    listener.onStudentResultClick(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestTakerVH {
        return TestTakerVH(
            ItemPersonResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TestTakerVH, position: Int) {
        holder.bind(position)
    }

    interface Listener {
        fun onStudentResultClick(model: TestResultDomain)
    }
}