package com.edu.test.presentation.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.edu.test.domain.model.dbModels.QuestionDomain
import com.edu.test.domain.model.dbModels.QuestionWithAnswersDomain
import com.edu.test.presentation.newQuestion.NewQuestionFragment
import com.edu.test.presentation.newQuestion.QuestionsPagerDiffUtil

class CreateQuestionsAdapter(private val fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val items: ArrayList<QuestionDomain> = arrayListOf()

    override fun getItemCount() = items.size

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return items.any { it.id.toLong() == itemId }
    }

    override fun createFragment(position: Int): Fragment {
        val question = items[position]
        val bundle = Bundle()
        bundle.putParcelable("question", question)
        return NewQuestionFragment.createInstance(bundle)
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val tag = "f" + holder.itemId
            val fragment = fragment.childFragmentManager.findFragmentByTag(tag)
            // safe check ,but fragment should not be null
            if (fragment != null) {
                (fragment as NewQuestionFragment).getSavedQuestionData(items[position])
            } else {
                super.onBindViewHolder(holder, position, payloads)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun setItems(newItems: List<QuestionDomain>) {
        val callback = QuestionsPagerDiffUtil(items, newItems)
        val diff = DiffUtil.calculateDiff(callback)

        items.clear()
        items.addAll(newItems)

        diff.dispatchUpdatesTo(this)
    }

}