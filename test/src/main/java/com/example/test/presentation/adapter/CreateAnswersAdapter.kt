package com.example.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.NewQuestionAnswerLayoutBinding
import com.example.test.presentation.model.NewAnswer


internal class CreateAnswersAdapter(private val listener: MarkAsCorrectClickListener) :
    RecyclerView.Adapter<CreateAnswersAdapter.AnswerVH>() {

    private var answersList: MutableList<NewAnswer> = mutableListOf()

    fun unMarkAllAnswers(){
        answersList = answersList.map {
            NewAnswer(it.title, false)
        }.toMutableList()
        notifyItemRangeChanged(0, answersList.size)
    }

    fun addAnswer(model: NewAnswer) {
        answersList.add(model)
        notifyItemInserted(answersList.size - 1)
    }

    fun updateAnswer(text: String, position: Int) {
        answersList[position] = NewAnswer(text)
        notifyItemChanged(position)
    }

    fun getAnswerByPosition(position: Int): String {
        return answersList[position].title
    }

    fun removeAnswer(position: Int) {
        answersList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun markAnswerAsCorrect(position: Int) {
        answersList[position] = NewAnswer(answersList[position].title, true)
        notifyItemChanged(position)
    }

    fun markAsIncorrect(position: Int) {
        answersList[position] = NewAnswer(answersList[position].title, false)
        notifyItemChanged(position)
    }

    fun getAllAnswers(): List<NewAnswer>{
        return answersList
    }

    inner class AnswerVH(val binding: NewQuestionAnswerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.newAnswer.text = answersList[position].title

            if (answersList[position].correctAnswer) {
                binding.root.setBackgroundResource(R.drawable.bg_outlined_green_400)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_outlined_white)
            }
            binding.root.setOnLongClickListener {
                listener.longClick(answersList[position], position)
                true
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

    override fun getItemCount(): Int {
        return answersList.size
    }

    interface MarkAsCorrectClickListener {
        fun longClick(model: NewAnswer, position: Int)
    }
}
