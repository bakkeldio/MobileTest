package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.test.databinding.ItemQuestionAddBinding
import com.edu.test.databinding.ItemQuestionIndicatorBinding
import com.edu.test.presentation.model.IndicatorItem

class QuestionsIndicatorAdapter(private val listener: QuestionsAdapterListener) :
    ListAdapter<IndicatorItem, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<IndicatorItem>() {
        override fun areItemsTheSame(oldItem: IndicatorItem, newItem: IndicatorItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IndicatorItem, newItem: IndicatorItem): Boolean {
            return oldItem == newItem
        }

    }) {


    companion object {
        private const val INDICATOR = 0
        private const val ADD = 1
    }

    fun updateIndicatorAt(oldPosition: Int, newPosition: Int) {
        val list = currentList.toMutableList()
        list[oldPosition] =
            IndicatorItem(list[oldPosition].id, false, oldPosition + 1)
        list[newPosition] =
            IndicatorItem(list[newPosition].id,  true,  newPosition + 1)
        submitList(list.toList())
    }


    inner class IndicatorVH(val binding: ItemQuestionIndicatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            with(getItem(position)) {
                binding.indicator.text = this.position.toString()
                binding.triangleImageView.isVisible = isCurrentPage
                binding.indicator.setOnClickListener {
                    listener.moveToPage(position)
                }
            }
        }
    }

    inner class AddQuestionBtnVH(val binding: ItemQuestionAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.addBtn.setOnClickListener {
                listener.createNewQuestion(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == INDICATOR) {
            IndicatorVH(
                ItemQuestionIndicatorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            AddQuestionBtnVH(
                ItemQuestionAddBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size - 1) {
            ADD
        } else {
            INDICATOR
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == currentList.size - 1) {
            (holder as AddQuestionBtnVH).bind(position)
        } else {
            (holder as IndicatorVH).bind(position)
        }
    }

    interface QuestionsAdapterListener {
        fun createNewQuestion(position: Int)
        fun moveToPage(position: Int)
    }
}