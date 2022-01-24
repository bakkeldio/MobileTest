package com.example.group.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.common.domain.group.model.GroupDomain
import com.example.common.presentation.ResourceState
import com.example.common.utils.DebounceQueryTextListener
import com.example.common.utils.addItemDecorationWithoutLastItem
import com.example.common.utils.showToast
import com.example.group.databinding.FragmentGroupsListBinding
import com.example.group.presentation.adapter.GroupsListAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
internal class GroupsListFragment : Fragment(), GroupsListAdapter.Listener {

    private var _binding: FragmentGroupsListBinding? = null
    private val binding: FragmentGroupsListBinding
        get() = _binding!!

    private val viewModel by viewModels<GroupsViewModel>()

    private val groupsAdapter by lazy {
        GroupsListAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.retrieveAndSaveUserRole()
        viewModel.getAllGroups()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.groupsRv.apply {
            adapter = groupsAdapter
            addItemDecorationWithoutLastItem()
        }

        viewModel.allGroupsState.observe(viewLifecycleOwner, { state ->
            binding.progress.root.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    groupsAdapter.submitList(state.data)
                }
                is ResourceState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        })

        viewModel.saveUserRoleState.observe(viewLifecycleOwner, { state ->
            if (state is ResourceState.Error) {
                showToast(state.message)
            }
        })

        binding.searchView.setOnQueryTextListener(DebounceQueryTextListener { query ->
            if (binding.searchView.hasFocus()) {
                viewModel.getAllGroups(query)
            }
        })

    }

    override fun onGroupClick(item: GroupDomain) {
        findNavController().navigate(
            GroupsListFragmentDirections.actionGroupListFragmentToGroupDetailFragment(
                item.uid
            )
        )
    }
}