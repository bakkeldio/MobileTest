package com.edu.mobiletest.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.group.domain.model.GroupDomain
import com.edu.mobiletest.databinding.ItemCompletedTestBinding

class TeacherGroupsAdapter(private val listener: (GroupDomain) -> Unit) : ListAdapter<GroupDomain, TeacherGroupsAdapter.TeacherGroupVH>(
    object : DiffUtil.ItemCallback<GroupDomain>(){
        override fun areItemsTheSame(oldItem: GroupDomain, newItem: GroupDomain): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: GroupDomain, newItem: GroupDomain): Boolean {
            return oldItem == newItem
        }

    }
) {


    inner class TeacherGroupVH(private val binding: ItemCompletedTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            with(getItem(position)){
                if (position > 0){
                    binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        setMargins(15,0,0,0)
                    }
                }
                binding.testName.text = groupName
                binding.root.setOnClickListener {
                    listener(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherGroupVH {
        return TeacherGroupVH(
            ItemCompletedTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TeacherGroupVH, position: Int) {
        holder.bind(position)
    }
}