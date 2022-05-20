package com.edu.group.presentation.groupDetailEdit

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.SelectImageBottomSheetDialog
import com.edu.common.utils.buildMaterialAlertDialog
import com.edu.common.utils.convertBitmapToUri
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.showToast
import com.edu.group.R
import com.edu.group.databinding.FragmentGroupDetailEditBinding
import com.edu.group.domain.model.GroupDomain
import com.edu.group.presentation.adapter.StudentsListAdapter
import com.edu.group.presentation.bottomsheet.StudentsBottomSheetFragment
import com.edu.group.presentation.model.StudentsAdapterType
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class GroupDetailEditFragment :
    BaseFragment<GroupDetailEditViewModel, FragmentGroupDetailEditBinding>(
        R.layout.fragment_group_detail_edit,
        FragmentGroupDetailEditBinding::bind
    ),
    StudentsListAdapter.DeleteStudent, SelectImageBottomSheetDialog.Listener {

    override val viewModel: GroupDetailEditViewModel by viewModels()

    @Inject
    lateinit var imageLoader: IImageLoader


    private val args by navArgs<GroupDetailEditFragmentArgs>()

    private var groupModel: GroupDomain? = null

    private val studentsAdapter by lazy {
        StudentsListAdapter(imageLoader, StudentsAdapterType.EDIT, this)
    }

    private var selectGroupImageBottomSheet: SelectImageBottomSheetDialog? = null

    private val selectImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectGroupImageBottomSheet = SelectImageBottomSheetDialog(it, this)
                selectGroupImageBottomSheet?.show(
                    childFragmentManager,
                    SelectImageBottomSheetDialog.TAG
                )
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getGroupDetails(args.groupId)
    }

    override fun setupUI() {
        binding.toolbar.setupWithNavController(findNavController())
        binding.studentsRV.adapter = studentsAdapter
        binding.addStudentBtn.setOnClickListener {
            StudentsBottomSheetFragment(args.groupId, viewModel, imageLoader).show(
                parentFragmentManager,
                ""
            )
        }
        binding.saveChanges.setOnClickListener {
            if (fieldsValid()) {
                groupModel?.let { model ->
                    viewModel.changeGroupDetails(
                        model.copy(
                            groupName = binding.groupNameEditText.text.toString(),
                            description = binding.groupDescriptionEditText.text.toString()
                        )
                    )
                }
            }
        }
        binding.changeAvatar.setOnClickListener {
            selectImageFromGallery.launch("image/*")
        }
    }

    override fun setupVM() {
        super.setupVM()
        viewModel.studentsList.observe(viewLifecycleOwner) {
            studentsAdapter.submitList(it)
        }
        viewModel.groupInfo.observe(viewLifecycleOwner) {
            groupModel = it
            binding.deleteAvatar.isVisible = it.avatar != null
            startLoadUserImage(it.avatar, false)
            binding.groupNameEditText.setText(it.groupName)
            binding.groupDescriptionEditText.setText(it.description)
        }
        viewModel.groupAvatar.observe(viewLifecycleOwner) { avatarUrl ->
            avatarUrl?.let {
                startLoadUserImage(avatarUrl, true)
            }
        }
        viewModel.groupDetailsUpdated.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().popBackStack()
            }
        }
    }

    private fun fieldsValid(): Boolean {
        if (binding.groupDescriptionEditText.text.isNullOrEmpty()) {
            showToast(resources.getString(R.string.group_description_should_not_be_empty))
            return false
        }

        if (binding.groupNameEditText.text.isNullOrEmpty()) {
            showToast(resources.getString(R.string.name_of_the_group_should_not_be_empty))
            return false
        }
        return true
    }

    override fun deleteStudent(student: StudentInfoDomain) {
        buildMaterialAlertDialog(resources.getString(R.string.do_you_want_to_delete_student_from_group),
            positiveBtnClick = {
                viewModel.deleteStudentFromGroup(args.groupId, student.uid)
            }).show()
    }

    override fun progressLoader(show: Boolean) {
        binding.loader.root.isVisible = show
    }

    private fun startLoadUserImage(url: String?, newImage: Boolean) {
        if (newImage) {
            imageLoader.loadImageCircleShapeAndSignature(
                url,
                binding.groupAvatar,
                Calendar.getInstance().timeInMillis,
                R.drawable.ic_fa6_solid_user_group
            )
        } else {
            imageLoader.loadImageWithCircleShape(
                url,
                binding.groupAvatar,
                R.drawable.ic_fa6_solid_user_group
            )
        }
    }

    override fun getCroppedImage(fileName: String, bitmap: Bitmap) {
        val uri = convertBitmapToUri(lifecycleScope, fileName, bitmap)
        viewModel.updateGroupAvatar(args.groupId, uri.toString())
    }

}