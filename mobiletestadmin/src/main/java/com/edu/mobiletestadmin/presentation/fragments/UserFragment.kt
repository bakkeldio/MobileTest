package com.edu.mobiletestadmin.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.databinding.FragmentUserBinding
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import com.edu.mobiletestadmin.presentation.viewModel.UserViewModel
import com.edu.mobiletestadmin.utils.*
import com.google.android.material.chip.Chip
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserFragment : Fragment(R.layout.fragment_user) {

    private val binding by viewBinding(FragmentUserBinding::bind)

    private val viewModel by viewModel<UserViewModel>()

    private var groupsBottomSheetFragment: AllGroupsBottomSheetFragment? = null

    private val args by navArgs<UserFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.groupId?.let { groups ->
            viewModel.getUserEmailAndGroups(args.userUid!!, groups.toList())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.userName?.let { user_name ->
            val firstName = user_name.substringBefore(" ")
            val lastName = user_name.substringAfter(" ")
            binding.nameEditText.setText(firstName)
            binding.surnameEditText.setText(lastName)
            binding.createBtn.text = resources.getString(R.string.update_user)
            binding.passwordField.hint =
                resources.getString(R.string.enter_new_password_if_you_want_update)

        }
        binding.toolbar.setupWithNavController(findNavController())

        if (args.userUid == null) {
            binding.chipGroup.addView(createAddBtn())
        }

        listenToFields()

        viewModel.newUserCreateLiveData.observe(viewLifecycleOwner) { state ->
            binding.progressLoader.root.isVisible = state is ResourceState.Loading
            binding.progressLoader.progressBar.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    findNavController().popBackStack()
                }
                is ResourceState.Error -> {
                    showToast(state.error)
                }
                else -> Unit
            }
        }

        viewModel.userGroups.observe(viewLifecycleOwner) { groups ->
            binding.chipGroup.removeAllViews()
            if (args.userType == UserTypeEnum.TEACHER || groups.size == 0) {
                binding.chipGroup.addView(createAddBtn())
            }

            groups.forEach {
                binding.chipGroup.addView(createChipForGroup(it.key, it.value.groupName))
            }
        }

        viewModel.email.observe(viewLifecycleOwner) { email ->
            binding.emailEditText.setText(email)
        }

        viewModel.userDetailsUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                showToast(resources.getString(R.string.info_updated))
            }
        }

        viewModel.userDeletedLiveData.observe(viewLifecycleOwner){ deleted ->
            if (deleted){
                findNavController().popBackStack()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            showToast(errorMessage)
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressLoader.root.isVisible = it
            binding.progressLoader.progressBar.isVisible = it
        }


        binding.createBtn.setOnClickListener {
            val isUpdate = args.userUid != null
            if (validateFields(isUpdate && binding.passwordEditText.text.isNullOrEmpty()))
                if (isUpdate) {
                    viewModel.updateUserDetail(
                        args.userUid!!,
                        binding.nameEditText.text.toString(),
                        binding.surnameEditText.text.toString(),
                        binding.emailEditText.text.toString(),
                        binding.passwordEditText.text.toString(),
                        args.userType
                    )
                } else {
                    viewModel.createNewUser(
                        binding.emailEditText.text.toString(),
                        binding.passwordEditText.text.toString(),
                        binding.nameEditText.text.toString(),
                        binding.surnameEditText.text.toString(),
                        args.userType
                    )
                }
        }
        binding.deleteBtn.setOnClickListener {
            viewModel.deleteUserByUid(args.userUid!!, args.userType)
        }

        binding.generatePasswordTextView.setOnClickListener {
            binding.passwordEditText.setText(viewModel.generateRandomUIDPassword())
        }

        binding.deleteBtn.isVisible = args.userUid != null

        KeyboardTriggerBehavior(this).observe(viewLifecycleOwner) { status ->
            if (status == KeyboardTriggerBehavior.Status.CLOSED){
                requireActivity().currentFocus?.clearFocus()
            }
        }

    }

    private fun createChipForGroup(groupUid: String, groupName: String) = Chip(context).apply {
        text = groupName
        tag = groupUid
        isCloseIconVisible = true
        setChipBackgroundColorResource(R.color.white)
        setChipStrokeColorResource(R.color.gray)
        chipStrokeWidth = 2f
        setCloseIconTintResource(R.color.main_color_pink)
        setTextColor(ContextCompat.getColor(context, R.color.black))
        setOnCloseIconClickListener {
            viewModel.removeFromGroup(this.tag as String)
        }
    }

    private fun createAddBtn() = Chip(context).apply {
        text = resources.getString(R.string.add_to_the_group)
        isCloseIconVisible = false
        setChipStrokeColorResource(R.color.gray)
        chipStrokeWidth = 2f
        setChipBackgroundColorResource(R.color.white)
        setChipIconTintResource(R.color.main_color_pink)
        chipIcon = ContextCompat.getDrawable(context, R.drawable.icon_add)
        setTextColor(ContextCompat.getColor(context, R.color.black))
        setOnClickListener {
            groupsBottomSheetFragment = AllGroupsBottomSheetFragment { group ->
                groupsBottomSheetFragment?.dismiss()
                viewModel.setNewUserGroup(group)
            }
            groupsBottomSheetFragment?.show(childFragmentManager, AllGroupsBottomSheetFragment.TAG)
        }
    }

    private fun validateFields(skipPasswordValidation: Boolean = false): Boolean {
        var isValid = true

        if (binding.nameEditText.text.isNullOrEmpty()) {
            isValid = false
            binding.nameField.isErrorEnabled = true
            binding.nameField.error = resources.getString(R.string.this_filed_cannot_be_empty)
        }
        if (binding.surnameEditText.text.isNullOrEmpty()) {
            isValid = false
            binding.surnameField.isErrorEnabled = true
            binding.surnameField.error = resources.getString(R.string.this_filed_cannot_be_empty)
        }

        if (!binding.emailEditText.text.toString().isValidEmail()) {
            isValid = false
            binding.emailField.isErrorEnabled = true
            binding.emailField.error = resources.getString(R.string.enter_valid_email)
        }

        if (!skipPasswordValidation) {
            if (!binding.passwordEditText.text.toString().isValidPassword()) {
                isValid = false
                binding.passwordField.isErrorEnabled = true
                binding.passwordField.error = resources.getString(R.string.enter_valid_password)
            }
        }

        return isValid
    }

    private fun listenToFields() {
        binding.nameEditText.addTextChangedListener {
            it?.let {
                binding.nameField.isErrorEnabled = false
                binding.nameField.error = null
            }
        }
        binding.surnameEditText.addTextChangedListener {
            it?.let {
                binding.surnameField.isErrorEnabled = false
                binding.surnameField.error = null
            }
        }
        binding.emailEditText.addTextChangedListener {
            it?.let {
                binding.emailField.isErrorEnabled = false
                binding.emailField.error = null
            }
        }
        binding.passwordEditText.addTextChangedListener {
            it?.let {
                binding.passwordField.isErrorEnabled = false
                binding.passwordField.error = null
            }
        }
    }

}