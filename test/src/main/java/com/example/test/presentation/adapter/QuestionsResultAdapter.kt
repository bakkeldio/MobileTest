package com.example.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.ItemQuestionResultBinding
import com.example.test.domain.model.QuestionResultDomain


internal class QuestionsResultAdapter :
    ListAdapter<QuestionResultDomain, QuestionsResultAdapter.QuestionVH>(Callback) {


    inner class QuestionVH(val binding: ItemQuestionResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).let {
                val adapter = QuestionAnswersAdapter()
                adapter.setData(it.answers)
                binding.questionAnswers.adapter = adapter
                val dividerItemDecor = DividerItemDecoration(binding.root.context, RecyclerView.VERTICAL)
                ResourcesCompat.getDrawable(binding.root.context.resources, R.drawable.divider_item, null)
                    ?.let { it1 -> dividerItemDecor.setDrawable(it1) }
                binding.questionAnswers.addItemDecoration(dividerItemDecor)
                binding.questionTextView.text = "${position+1}. ${it.questionTitle}"
                binding.correctAnswerTextView.text =
                    binding.root.resources.getString(R.string.correct_answers)
                        .format(it.correctAnswers.joinToString())
                binding.pointTextView.text = it.questionPoint.toString()
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