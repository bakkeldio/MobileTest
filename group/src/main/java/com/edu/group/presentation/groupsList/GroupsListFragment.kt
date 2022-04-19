package com.edu.group.presentation.groupsList

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.edu.common.presentation.BaseFragment
import com.edu.common.utils.DebounceQueryTextListener
import com.edu.common.utils.addItemDecorationWithoutLastItem
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.group.R
import com.edu.group.databinding.FragmentGroupsListBinding
import com.edu.group.domain.model.GroupDomain
import com.edu.group.presentation.adapter.GroupsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GroupsListFragment : BaseFragment<GroupsViewModel, FragmentGroupsListBinding>(
    R.layout.fragment_groups_list,
    FragmentGroupsListBinding::bind
), GroupsListAdapter.Listener {


    @Inject
    lateinit var imageLoader: IImageLoader

    override val viewModel by viewModels<GroupsViewModel>()

    private val groupsAdapter by lazy {
        GroupsListAdapter(this, imageLoader)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.retrieveAndSaveUserRole()
        viewModel.getAllGroups()
    }

    override fun onGroupClick(item: GroupDomain) {
        findNavController().navigate(
            GroupsListFragmentDirections.actionGroupListFragmentToGroupDetailFragment(
                item.uid
            )
        )
    }

    override fun progressLoader(show: Boolean) {
        binding.progress.root.isVisible = show
    }

    override fun setupUI() {
        binding.toolbar.setupWithNavController(findNavController())
        binding.groupsRv.apply {
            adapter = groupsAdapter
            addItemDecorationWithoutLastItem()
        }

        (binding.toolbar.menu.findItem(R.id.action_search).actionView as SearchView).apply {
            setOnQueryTextListener(DebounceQueryTextListener { query ->
                if (hasFocus()) {
                    viewModel.getAllGroups(query)
                }
            })
        }
    }

    override fun setupVM() {
        super.setupVM()
        viewModel.allGroupsState.observe(viewLifecycleOwner) { result ->
            groupsAdapter.submitList(result)
        }
    }
}