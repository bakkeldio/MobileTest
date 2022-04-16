package com.edu.group.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.group.R
import com.edu.group.databinding.ItemOtherStudentBinding
import com.edu.group.databinding.ItemTopStudentBinding


class GroupStudentsRatingAdapter :
    ListAdapter<StudentInfoDomain, RecyclerView.ViewHolder>(ItemCallback) {



    inner class TopStudentsVH(val binding: ItemTopStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.award.background = when (position) {
                    FIRST_POSITION -> ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_group_first_place
                    )
                    SECOND_POSITION -> ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_group_second_place
                    )
                    else -> ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_group_third_place
                    )
                }
                binding.fullName.text = name
                binding.totalPoint.text = overallScore.toString()
            }
        }
    }

    inner class StudentVH(val binding: ItemOtherStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.name.text = name
                binding.number.text = "$position."
                binding.points.text = overallScore.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.TOP_STUDENT.value) {
            TopStudentsVH(
                ItemTopStudentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            StudentVH(
                ItemOtherStudentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (ViewType.getByPosition(position) == ViewType.TOP_STUDENT && holder is TopStudentsVH) {
            holder.bind(position)
        } else if (holder is StudentVH) {
            holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ViewType.getByPosition(position).value
    }

    enum class ViewType(val value: Int) {
        TOP_STUDENT(0),
        STUDENT(1);

        companion object {
            fun getByPosition(position: Int): ViewType {
                return if (position in FIRST_POSITION..THIRD_POSITION) {
                    TOP_STUDENT
                } else {
                    STUDENT
                }
            }
        }
    }

    companion object {
        private const val FIRST_POSITION = 0
        private const val SECOND_POSITION = 1
        private const val THIRD_POSITION = 2
    }

    object ItemCallback : DiffUtil.ItemCallback<StudentInfoDomain>() {
        override fun areItemsTheSame(
            oldItem: StudentInfoDomain,
            newItem: StudentInfoDomain
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: StudentInfoDomain,
            newItem: StudentInfoDomain
        ): Boolean {
            return oldItem == newItem
        }

    }
}