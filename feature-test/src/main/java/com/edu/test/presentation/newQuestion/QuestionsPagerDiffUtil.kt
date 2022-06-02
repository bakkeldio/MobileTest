package com.edu.test.presentation.newQuestion

import androidx.recyclerview.widget.DiffUtil
import com.edu.test.domain.model.dbModels.QuestionDomain
import com.edu.test.domain.model.dbModels.QuestionWithAnswersDomain

class QuestionsPagerDiffUtil(
    private val oldList: List<QuestionDomain>,
    private val newList: List<QuestionDomain>
) : DiffUtil.Callback() {

    enum class PayloadKey {
        VALUE
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        return listOf(PayloadKey.VALUE)
    }
}