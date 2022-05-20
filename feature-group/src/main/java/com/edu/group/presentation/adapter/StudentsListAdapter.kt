package com.edu.group.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.group.databinding.ItemStudentBinding
import com.edu.group.databinding.ItemStudentEditBinding
import com.edu.group.presentation.model.StudentsAdapterType

class StudentsListAdapter(
    val imageLoader: IImageLoader,
    private val studentsAdapterType: StudentsAdapterType,
    private val deleteStudent: DeleteStudent? = null,
    private val addStudent: AddStudent? = null
) :
    ListAdapter<StudentInfoDomain, RecyclerView.ViewHolder>(Callback) {

    inner class StudentVH(val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.studentName.text = name
                imageLoader.loadImageWithCircleShape(
                    avatarUrl, binding.avatar
                )
                binding.addBtn.isVisible = studentsAdapterType == StudentsAdapterType.ADD
                binding.addBtn.setOnClickListener {
                    addStudent?.invoke(this)
                }
            }
        }

    }

    inner class StudentEditVH(val binding: ItemStudentEditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                binding.studentName.text = name
                binding.deleteIcon.setOnClickListener {
                    deleteStudent?.deleteStudent(this)
                }
                imageLoader.loadImageWithCircleShape(avatarUrl, binding.studentLogo)
            }
        }
    }

    object Callback : DiffUtil.ItemCallback<StudentInfoDomain>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (studentsAdapterType == StudentsAdapterType.NORMAL || studentsAdapterType == StudentsAdapterType.ADD) {
            StudentVH(
                ItemStudentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            StudentEditVH(
                ItemStudentEditBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (studentsAdapterType == StudentsAdapterType.NORMAL || studentsAdapterType == StudentsAdapterType.ADD) {
            if (holder is StudentVH) {
                holder.bind(position)
            }
        } else {
            if (holder is StudentEditVH) {
                holder.bind(position)
            }
        }
    }

    interface DeleteStudent {
        fun deleteStudent(student: StudentInfoDomain)
    }

    interface AddStudent{
        operator fun invoke(student: StudentInfoDomain)
    }
}