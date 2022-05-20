package com.edu.test.presentation.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.edu.common.domain.model.QuestionDomain
import com.edu.test.data.datamanager.TestProcessHandler
import com.edu.test.presentation.question.QuestionFragment

class QuestionsPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val questionsList: MutableList<QuestionDomain> = mutableListOf()

    fun submitData(data: List<QuestionDomain>) {
        questionsList.clear()
        questionsList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        val question = questionsList[position]
        bundle.putParcelable("question", questionsList[position])
        bundle.putParcelable("questionResult", TestProcessHandler.getUserAnswers()[question.uid])
        bundle.putInt("position", position)
        return QuestionFragment.createInstance(bundle)
    }
}