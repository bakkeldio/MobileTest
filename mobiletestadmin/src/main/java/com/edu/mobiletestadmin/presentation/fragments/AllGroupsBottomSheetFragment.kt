package com.edu.mobiletestadmin.presentation.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.databinding.FragmentAllGroupsFragmentBinding
import com.edu.mobiletestadmin.presentation.adapter.GroupsAdapter
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.presentation.viewModel.GroupsViewModel
import com.edu.mobiletestadmin.utils.IImageLoader
import com.edu.mobiletestadmin.utils.showToast
import com.edu.mobiletestadmin.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllGroupsBottomSheetFragment(private val onGroupClick: (UserGroup) -> Unit) :
    BottomSheetDialogFragment(), GroupsAdapter.Listener {

    private val binding by viewBinding(FragmentAllGroupsFragmentBinding::bind)

    private val viewModel by viewModel<GroupsViewModel>()

    private val imageLoader by inject<IImageLoader>()

    private val groupsAdapter by lazy {
        GroupsAdapter(this, imageLoader)
    }

    companion object {
        const val TAG = "All_Groups_Bottom_Sheet_Fragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_all_groups_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getGroups()
        binding.recyclerView.adapter = groupsAdapter

        viewModel.groupsLiveData.observe(viewLifecycleOwner) { state ->
            binding.progressLoader.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    groupsAdapter.submitList(state.data)
                }
                is ResourceState.Error -> {
                    showToast(state.error)
                }
                is ResourceState.Empty -> {
                    binding.noGroupsMessage.isVisible = true
                }
                else -> Unit
            }

        }
    }

    override fun onGroupClick(group: UserGroup) {
        onGroupClick.invoke(group)
    }

}