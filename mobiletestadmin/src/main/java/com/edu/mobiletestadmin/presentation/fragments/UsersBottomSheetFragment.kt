package com.edu.mobiletestadmin.presentation.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.databinding.FragmentUsersBottomSheetBinding
import com.edu.mobiletestadmin.presentation.adapter.UsersAdapter
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import com.edu.mobiletestadmin.presentation.viewModel.UsersViewModel
import com.edu.mobiletestadmin.utils.IImageLoader
import com.edu.mobiletestadmin.utils.showToast
import com.edu.mobiletestadmin.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class UsersBottomSheetFragment(
    private val usersType: UserTypeEnum,
    private val userClick: (User) -> Unit
) : BottomSheetDialogFragment(),
    UsersAdapter.Listener {

    private val viewModel by viewModel<UsersViewModel>()

    private val binding by viewBinding(FragmentUsersBottomSheetBinding::bind)

    private val imageLoader by inject<IImageLoader>()

    private val usersAdapter by lazy { UsersAdapter(this, imageLoader) }

    companion object {
        const val TAG = "USERS_BOTTOM_SHEET_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_users_bottom_sheet, container, false)
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
        when (usersType) {
            UserTypeEnum.STUDENT -> {
                viewModel.getStudents(true)
            }
            UserTypeEnum.TEACHER -> {
                viewModel.getTeachers()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usersRV.adapter = usersAdapter
        viewModel.studentsLiveData.observe(viewLifecycleOwner) { state ->
            binding.progressLoader.isVisible = state is ResourceState.Loading
            binding.message.isVisible = state is ResourceState.Empty
            when (state) {
                is ResourceState.Success -> {
                    usersAdapter.submitList(state.data)
                }
                is ResourceState.Error -> {
                    showToast(state.error)
                }
                is ResourceState.Empty -> {
                    binding.message.text = resources.getString(R.string.there_is_not_any_students)
                    usersAdapter.submitList(emptyList())
                }
            }
        }
        viewModel.teachersLiveData.observe(viewLifecycleOwner) { state ->
            binding.progressLoader.isVisible = state is ResourceState.Loading
            binding.message.isVisible = state is ResourceState.Empty
            when (state) {
                is ResourceState.Success -> {
                    usersAdapter.submitList(state.data)
                }
                is ResourceState.Error -> {
                    showToast(state.error)
                }
                is ResourceState.Empty -> {
                    binding.message.text = resources.getString(R.string.there_is_not_any_teachers)
                }
                else -> Unit
            }

        }

    }

    override fun onUserClicked(user: User) {
        userClick(user)
    }
}