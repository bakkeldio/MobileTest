package com.edu.group.presentation.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.edu.common.utils.addItemDecorationWithoutLastItem
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.showToast
import com.edu.common.utils.viewBinding
import com.edu.group.R
import com.edu.group.databinding.FragmentGroupStudentsBottomsheetBinding
import com.edu.group.presentation.adapter.StudentsListAdapter
import com.edu.group.presentation.groupDetail.GroupDetailViewModel
import com.edu.group.presentation.model.StudentsAdapterType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class GroupStudentsBottomSheetFragment(
    private val groupId: String,
    private val viewModel: GroupDetailViewModel,
    private val imageLoader: IImageLoader
) : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentGroupStudentsBottomsheetBinding::bind)

    companion object {
        const val TAG = "GroupStudentsBottomSheet"
    }

    private val studentsListAdapter by lazy {
        StudentsListAdapter(imageLoader, StudentsAdapterType.NORMAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_group_students_bottomsheet, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStudents(groupId)
        binding.recyclerView.adapter = studentsListAdapter
        binding.recyclerView.addItemDecorationWithoutLastItem(R.id.avatar)
        viewModel.students.observe(viewLifecycleOwner) { studentsList ->
            binding.emptyDataMessageTextView.isVisible = studentsList.isEmpty()
            studentsListAdapter.submitList(studentsList)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }
}
