package com.example.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test.databinding.ItemTestBinding
import com.example.common.domain.test.model.TestDomainModel
import java.text.SimpleDateFormat
import java.util.*

internal class TestsAdapter(val listener: ItemClickListener): ListAdapter<TestDomainModel, TestsAdapter.TestViewHolder>(ItemCallback) {


    inner class TestViewHolder(private val binding: ItemTestBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            getItem(position).apply {
                binding.root.setOnClickListener {
                    listener.onItemClick(getItem(position))
                }
                binding.testTitle.text = getItem(position).title
                val dayMonthFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
                val hourMinuteFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = "${dayMonthFormat.format(date)} ${hourMinuteFormat.format(date)}"
                binding.dateTime.text = date
            }
        }
    }

    object ItemCallback: DiffUtil.ItemCallback<TestDomainModel>(){
        override fun areItemsTheSame(oldItem: TestDomainModel, newItem: TestDomainModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: TestDomainModel,
            newItem: TestDomainModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        return TestViewHolder(ItemTestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(position)
    }

    internal interface ItemClickListener{
        fun onItemClick(model: TestDomainModel)
    }
}