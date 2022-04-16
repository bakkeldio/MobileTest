package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.domain.model.QuestionType
import com.edu.test.databinding.ItemQuestionStateBinding
import com.edu.test.presentation.model.PageModel

class TestPagesAdapter(private val pageListener: PageListener) :
    RecyclerView.Adapter<TestPagesAdapter.PageViewHolder>() {

    private val pages: MutableList<PageModel> = mutableListOf()

    fun updateItem(position: Int, check: Boolean) {
        if ((!check && pages[position].checked) || (check && !pages[position].checked)){
            pages[position] = PageModel(check)
            notifyItemChanged(position)
        }
    }

    fun submitData(list: List<PageModel>) {
        pages.clear()
        pages.addAll(list)
        notifyDataSetChanged()
    }

    inner class PageViewHolder(private val binding: ItemQuestionStateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = pages[position]
            binding.questionNum.text = "${position + 1}. "
            binding.emptyBtn.isVisible = !item.checked
            binding.filledBtn.isVisible = item.checked
            binding.emptyBtn.setOnClickListener {
                pageListener.onPageClick(position)
            }
            binding.filledBtn.setOnClickListener {
                pageListener.onPageClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return PageViewHolder(
            ItemQuestionStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    interface PageListener {
        fun onPageClick(position: Int)
    }

}