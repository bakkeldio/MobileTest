package com.example.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.presentation.model.TestModel
import com.example.common.presentation.model.TestStatusEnum
import com.example.test.R
import com.example.test.databinding.ItemTestBinding
import java.text.SimpleDateFormat
import java.util.*

class TestsAdapter(
    val listener: ItemClickListener,
    private val adapterTypeEnum: TestsAdapterTypeEnum
) :
    ListAdapter<TestModel, TestsAdapter.TestViewHolder>(ItemCallback) {


    inner class TestViewHolder(private val binding: ItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.root.setOnClickListener {
                    listener.onItemClick(getItem(position))
                }
                binding.testTitle.text = getItem(position).title
                if (adapterTypeEnum == TestsAdapterTypeEnum.ALL_TESTS) {
                    binding.statusTextView.text = status.status
                    if (status == TestStatusEnum.IN_PROGRESS) {
                        binding.statusTextView.setBackgroundColor(binding.root.resources.getColor(R.color.green))
                    }
                } else {
                    binding.statusTextView.isVisible = false
                }
                val dayMonthFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
                val hourMinuteFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = "${dayMonthFormat.format(date)} ${hourMinuteFormat.format(date)}"
                binding.dateTime.text = date
            }
        }
    }

    object ItemCallback : DiffUtil.ItemCallback<TestModel>() {
        override fun areItemsTheSame(oldItem: TestModel, newItem: TestModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: TestModel,
            newItem: TestModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        return TestViewHolder(
            ItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(position)
    }

    interface ItemClickListener {
        fun onItemClick(model: TestModel)
    }
}