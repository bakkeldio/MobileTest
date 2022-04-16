package com.edu.group.presentation.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.presentation.ResourceState
import com.edu.common.utils.addItemDecorationWithoutLastItem
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.viewBinding
import com.edu.group.R
import com.edu.group.databinding.FragmentStudentsBottomsheetBinding
import com.edu.group.presentation.adapter.StudentsListAdapter
import com.edu.group.presentation.groupDetailEdit.GroupDetailEditViewModel
import com.edu.group.presentation.model.StudentsAdapterType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StudentsBottomSheetFragment(
    private val groupId: String,
    private val viewModel: GroupDetailEditViewModel,
    private val imageLoader: IImageLoader
) : BottomSheetDialogFragment(), StudentsListAdapter.AddStudent {

    val binding by viewBinding(FragmentStudentsBottomsheetBinding::bind)

    private val adapter by lazy {
        StudentsListAdapter(imageLoader, StudentsAdapterType.ADD, addStudent = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_students_bottomsheet, container, false)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getStudentsToAdd()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allStudentsRV.adapter = adapter
        binding.allStudentsRV.addItemDecorationWithoutLastItem()
        viewModel.studentsToAddList.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state is ResourceState.Loading
            binding.availableStudentsMsgTextView.isVisible = state is ResourceState.Empty
            if (state is ResourceState.Success) {
                adapter.submitList(state.data)
            }
            if (state is ResourceState.Empty){
                adapter.submitList(emptyList())
            }
        }
        viewModel.studentAdd.observe(viewLifecycleOwner) {
            binding.progress.isVisible = it is ResourceState.Loading
        }

    }

    override fun invoke(student: StudentInfoDomain) {
        viewModel.addStudent(groupId, student)
    }

}