package com.example.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.test.databinding.ItemQuestionAddBinding
import com.example.test.databinding.ItemQuestionIndicatorBinding
import com.example.test.presentation.model.IndicatorItem

class QuestionsIndicatorAdapter(private val listener: QuestionsAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list: MutableList<IndicatorItem> = mutableListOf()

    companion object {
        private const val INDICATOR = 0
        private const val ADD = 1
    }

    init {
        repeat(2) {
            list.add(IndicatorItem())
        }
        notifyDataSetChanged()
    }

    fun addIndicatorAtPosition(position: Int) {
        list.add(position, IndicatorItem())
        notifyItemInserted(position)
        notifyItemRangeChanged(position, 2)
    }

    fun removeIndicatorAtPosition(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        list[position] = IndicatorItem(true)
        notifyItemRangeChanged(position, list.size - position)
    }

    fun updateIndicatorAt(oldPosition: Int, newPosition: Int) {
        list[oldPosition] = IndicatorItem(false)
        list[newPosition] = IndicatorItem(true)
        notifyItemChanged(oldPosition)
        notifyItemChanged(newPosition)
    }

    inner class IndicatorVH(val binding: ItemQuestionIndicatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.indicator.text = (position + 1).toString()
            binding.triangleImageView.isVisible = list[position].isCurrentPage
            binding.indicator.setOnClickListener {
                listener.moveToPage(position)
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
        return if (position == list.size - 1) {
            ADD
        } else {
            INDICATOR
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == list.size - 1) {
            (holder as AddQuestionBtnVH).bind(position)
        } else {
            (holder as IndicatorVH).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface QuestionsAdapterListener {
        fun createNewQuestion(position: Int)
        fun moveToPage(position: Int)
    }
}