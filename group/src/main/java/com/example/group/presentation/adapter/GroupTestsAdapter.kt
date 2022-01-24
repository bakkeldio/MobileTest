package com.example.group.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.common.domain.test.model.TestDomainModel
import com.example.group.databinding.ItemTestGridBinding

class GroupTestsAdapter(val itemClick: (TestDomainModel) -> Unit) : RecyclerView.Adapter<GroupTestsAdapter.GroupTestVH>() {

    private val groupTestsList: MutableList<TestDomainModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitTests(tests: List<TestDomainModel>) {
        groupTestsList.addAll(tests)
        notifyDataSetChanged()
    }

    inner class GroupTestVH(private val binding: ItemTestGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.testTitle.text = groupTestsList[position].title
            binding.root.setOnClickListener {
                itemClick(groupTestsList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupTestVH {
        return GroupTestVH(
            ItemTestGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupTestVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return groupTestsList.size
    }
}