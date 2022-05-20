package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.test.R
import com.edu.test.databinding.ItemAnswerVarBinding
import com.edu.test.presentation.model.ItemAnswer


class AnswersAdapter :
    ListAdapter<ItemAnswer, AnswersAdapter.AnswerVH>(object : DiffUtil.ItemCallback<ItemAnswer>() {
        override fun areItemsTheSame(oldItem: ItemAnswer, newItem: ItemAnswer): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: ItemAnswer, newItem: ItemAnswer): Boolean {
            return oldItem == newItem
        }

    }) {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    inner class AnswerVH(val binding: ItemAnswerVarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, isActivated: Boolean) {
            getItem(position).apply {
                binding.answerVar.text = "$key. $answer"
                if (isActivated) {
                    binding.answerVar.setBackgroundResource(R.drawable.bg_outlined_pink)
                } else {
                    binding.answerVar.setBackgroundResource(R.drawable.bg_outlined_gray)
                }
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Long {
                    return adapterPosition.toLong()
                }

                override fun inSelectionHotspot(e: MotionEvent): Boolean {
                    return true
                }

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerVH {
        return AnswerVH(
            ItemAnswerVarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnswerVH, position: Int) {
        tracker?.let {
            holder.bind(position, it.isSelected(position.toLong()))
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyItemsDetailsLookUp(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as AnswersAdapter.AnswerVH).getItemDetails()
            }
            return null
        }

    }

}