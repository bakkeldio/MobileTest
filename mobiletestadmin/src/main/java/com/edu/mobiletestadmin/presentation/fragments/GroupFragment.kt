package com.edu.mobiletestadmin.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.databinding.FragmentGroupBinding
import com.edu.mobiletestadmin.presentation.model.GroupFragmentTypeEnum
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.viewModel.GroupViewModel
import com.edu.mobiletestadmin.utils.IImageLoader
import com.edu.mobiletestadmin.utils.KeyboardTriggerBehavior
import com.edu.mobiletestadmin.utils.showToast
import com.edu.mobiletestadmin.utils.viewBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class GroupFragment : Fragment(R.layout.fragment_group) {

    private val binding by viewBinding(FragmentGroupBinding::bind)

    private val viewModel by viewModel<GroupViewModel>()

    private var lastSelectedUri: String? = null

    private val imageLoader: IImageLoader by inject()

    private val args by navArgs<GroupFragmentArgs>()
    private val logoChooser =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                if (args.groupFragmentType == GroupFragmentTypeEnum.EXISTING_GROUP) {
                    viewModel.updateGroupLogo(args.groupId!!, it.toString())
                } else {
                    this.lastSelectedUri = it.toString()
                    imageLoader.loadImageWithUriAndCircleShape(
                        it,
                        binding.groupLogo,
                        R.drawable.ic_fa6_solid_user_group
                    )
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (args.groupFragmentType) {
            GroupFragmentTypeEnum.CREATE_GROUP -> {
            }
            GroupFragmentTypeEnum.EXISTING_GROUP -> {
                viewModel.getGroupInfo(args.groupId!!)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNavController(findNavController())
        listenToFields()

        binding.updateGroupButton.isVisible =
            args.groupFragmentType == GroupFragmentTypeEnum.EXISTING_GROUP
        binding.groupMembersTextView.isVisible =
            args.groupFragmentType == GroupFragmentTypeEnum.EXISTING_GROUP

        binding.createBtn.text = when (args.groupFragmentType) {
            GroupFragmentTypeEnum.EXISTING_GROUP -> resources.getString(R.string.delete)
            GroupFragmentTypeEnum.CREATE_GROUP -> resources.getString(R.string.create_group)
        }

        binding.groupMembersTextView.setOnClickListener {
            findNavController().navigate(
                GroupFragmentDirections.fromGroupFragmentToGroupUsersFragment(
                    args.groupId!!,
                    binding.groupNameEditText.text.toString()
                )
            )
        }

        binding.updateGroupButton.setOnClickListener {
            if (validGroupNameField()) {
                viewModel.updateGroup(
                    args.groupId!!,
                    binding.groupNameEditText.text.toString(),
                    binding.groupDescriptionEditText.text.toString()
                )
            }
        }

        binding.createBtn.setOnClickListener {
            if (args.groupFragmentType == GroupFragmentTypeEnum.CREATE_GROUP) {
                if (validGroupNameField()) {
                    viewModel.createGroup(
                        binding.groupNameEditText.text.toString(),
                        binding.groupDescriptionEditText.text.toString(),
                        lastSelectedUri
                    )
                }
            } else {
                viewModel.deleteGroup(args.groupId!!)
            }
        }

        binding.changeLogo.setOnClickListener {
            logoChooser.launch("image/*")
        }

        binding.deleteLogo.setOnClickListener {
            viewModel.deleteGroupLogo(args.groupId!!)
        }
        viewModel.groupInfoLiveData.observe(viewLifecycleOwner) { state ->
            binding.loader.root.isVisible = state is ResourceState.Loading
            binding.loader.progressBarCenter.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    binding.deleteLogo.isVisible = state.data.groupAvatar != null
                    binding.groupNameEditText.setText(state.data.groupName)
                    binding.groupDescriptionEditText.setText(state.data.description)
                    imageLoader.loadImageWithCircleShape(
                        binding.groupLogo,
                        state.data.groupAvatar,
                        R.drawable.ic_fa6_solid_user_group
                    )
                }
                is ResourceState.Error -> {
                    showToast(state.error)
                }
                else -> Unit
            }
        }
        viewModel.groupCreatedLiveData.observe(viewLifecycleOwner) { state ->
            binding.loader.root.isVisible = state is ResourceState.Loading
            binding.loader.progressBar.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    findNavController().navigate(
                        GroupFragmentDirections.fromGroupFragmentToGroupUsersFragment(
                            state.data,
                            binding.groupNameEditText.text.toString()
                        )
                    )
                }
                is ResourceState.Error -> {
                    showToast(state.error)
                }
                else -> Unit
            }
        }

        viewModel.groupLogo.observe(viewLifecycleOwner) { state ->
            binding.loader.root.isVisible = state is ResourceState.Loading
            binding.loader.progressBarCenter.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    imageLoader.loadImageWithCircleShapeAndSignature(
                        binding.groupLogo,
                        state.data,
                        Calendar.getInstance().timeInMillis,
                        R.drawable.ic_user_placeholder
                    )
                }
                is ResourceState.Error -> {
                    showToast(state.error)
                }
                else -> Unit
            }
        }
        viewModel.groupDeleted.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().popBackStack()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.loader.root.isVisible = it
            binding.loader.progressBarCenter.isVisible = it
        }

        KeyboardTriggerBehavior(this).observe(viewLifecycleOwner) { status ->
            if (status == KeyboardTriggerBehavior.Status.CLOSED) {
                requireActivity().currentFocus?.clearFocus()
            }
        }

    }

    private fun validGroupNameField(): Boolean {
        var isValid = true

        if (binding.groupNameEditText.text.isNullOrEmpty()) {
            isValid = false
            binding.groupNameField.isErrorEnabled = true
            binding.groupNameField.error = resources.getString(R.string.this_filed_cannot_be_empty)
        }

        if (binding.groupDescriptionEditText.text.isNullOrEmpty()) {
            isValid = false
            binding.groupDescriptionField.isErrorEnabled = true
            binding.groupDescriptionField.error =
                resources.getString(R.string.this_filed_cannot_be_empty)
        }

        return isValid
    }

    private fun listenToFields() {
        binding.groupNameEditText.addTextChangedListener {
            it?.let {
                binding.groupNameField.isErrorEnabled = false
                binding.groupNameField.error = null
            }
        }

        binding.groupDescriptionEditText.addTextChangedListener {
            it?.let {
                binding.groupDescriptionField.isErrorEnabled = false
                binding.groupDescriptionField.error = null
            }
        }
    }

}