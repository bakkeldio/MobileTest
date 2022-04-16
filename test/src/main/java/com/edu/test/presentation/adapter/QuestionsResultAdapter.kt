package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.test.R
import com.edu.test.databinding.ItemQuestionResultBinding
import com.edu.test.domain.model.QuestionResultDomain


internal class QuestionsResultAdapter :
    ListAdapter<QuestionResultDomain, QuestionsResultAdapter.QuestionVH>(Callback) {


    inner class QuestionVH(val binding: ItemQuestionResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).let { question ->
                binding.questionTextView.text = "${position + 1}. ${question.questionTitle}"
                binding.pointTextView.text = question.questionPoint.toString()

                binding.questionAnswers.isVisible = question.answerForOpenQuestion == null
                binding.openQuestionText.isVisible = question.answerForOpenQuestion != null
                binding.correctAnswerTextView.isVisible = question.answerForOpenQuestion == null
                if (question.answerForOpenQuestion != null){
                    binding.openQuestionText.text = question.answerForOpenQuestion
                }else {
                    val adapter = QuestionAnswersAdapter()
                    adapter.setData(question.answers)
                    binding.questionAnswers.adapter = adapter
                    val dividerItemDecor =
                        DividerItemDecoration(binding.root.context, RecyclerView.VERTICAL)
                    ResourcesCompat.getDrawable(
                        binding.root.context.resources,
                        R.drawable.divider_item,
                        null
                    )
                        ?.let { it1 -> dividerItemDecor.setDrawable(it1) }
                    binding.questionAnswers.addItemDecoration(dividerItemDecor)
                    binding.correctAnswerTextView.text =
                        binding.root.resources.getString(R.string.correct_answers)
                            .format(question.correctAnswers.joinToString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionVH {
        return QuestionVH(
            ItemQuestionResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: QuestionVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    object Callback : DiffUtil.ItemCallback<QuestionResultDomain>() {
        override fun areItemsTheSame(
            oldItem: QuestionResultDomain,
            newItem: QuestionResultDomain
        ): Boolean {
            return oldItem.questionUid == newItem.questionUid
        }

        override fun areContentsTheSame(
            oldItem: QuestionResultDomain,
            newItem: QuestionResultDomain
        ): Boolean {
            return oldItem == newItem
        }

    }

}