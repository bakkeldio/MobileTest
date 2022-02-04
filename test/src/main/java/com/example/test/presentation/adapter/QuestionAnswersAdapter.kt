package com.example.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.common.utils.setForegroundColorSpan
import com.example.test.R
import com.example.test.databinding.ItemAnswerBinding
import com.example.test.domain.model.AnswerDomain

class QuestionAnswersAdapter : RecyclerView.Adapter<QuestionAnswersAdapter.AnswerVH>() {


    private val answersList: MutableList<AnswerDomain> = mutableListOf()

    fun setData(list: List<AnswerDomain>) {
        answersList.clear()
        answersList.addAll(list)
        notifyDataSetChanged()
    }

    inner class AnswerVH(val binding: ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val model = answersList[position]
            binding.answer.text = model.title

            if (model.isSelected) {
                binding.answer.text =
                    model.title.setForegroundColorSpan(
                        if (model.isCorrect) R.color.green else R.color.red,
                        binding.root.context
                    )
                binding.answer.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    if (model.isCorrect) R.drawable.ic_baseline_check_24 else R.drawable.ic_baseline_close_24,
                    0
                )
            } else {
                binding.answer.text = model.title.setForegroundColorSpan(
                    R.color.main_text_color,
                    binding.root.context
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerVH {
        return AnswerVH(
            ItemAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnswerVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return answersList.size
    }

}