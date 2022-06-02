package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.test.R
import com.edu.test.databinding.NewQuestionAnswerLayoutBinding
import com.edu.test.domain.model.dbModels.QuestionAnswerDomain


internal class CreateAnswersAdapter(private val listener: MarkAsCorrectClickListener) :
    ListAdapter<QuestionAnswerDomain, CreateAnswersAdapter.AnswerVH>(object :
        DiffUtil.ItemCallback<QuestionAnswerDomain>() {
        override fun areItemsTheSame(
            oldItem: QuestionAnswerDomain,
            newItem: QuestionAnswerDomain
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: QuestionAnswerDomain,
            newItem: QuestionAnswerDomain
        ): Boolean {
            return oldItem == newItem
        }

    }) {


    inner class AnswerVH(val binding: NewQuestionAnswerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            with(getItem(position)) {
                binding.newAnswer.text = title

                if (isCorrect) {
                    binding.root.setBackgroundResource(R.drawable.bg_outlined_green_400)
                } else {
                    binding.root.setBackgroundResource(R.drawable.bg_outlined_white)
                }
                binding.root.setOnLongClickListener {
                    listener.longClick(getItem(position), position)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerVH {
        return AnswerVH(
            NewQuestionAnswerLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnswerVH, position: Int) {
        holder.bind(position)
    }


    interface MarkAsCorrectClickListener {
        fun longClick(model: QuestionAnswerDomain, position: Int)
    }
}
