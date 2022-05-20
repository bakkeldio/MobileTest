package com.edu.test.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.edu.test.presentation.newQuestion.NewQuestionFragment

class CreateQuestionsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val list: MutableList<NewQuestionFragment> = mutableListOf()

    fun createNewPage(position: Int){
        list.add(NewQuestionFragment())
        notifyItemInserted(position)
    }

    fun removeNewPage(position: Int){
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    init {
        list.add(NewQuestionFragment())
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}