package com.edu.group.presentation.groupDetail

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.edu.common.navigation.Navigation
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.ResourceState
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.showToast
import com.edu.group.R
import com.edu.group.databinding.FragmentGroupDetailBinding
import com.edu.group.domain.model.CoreRoleEnum
import com.edu.group.presentation.adapter.GroupTestsAdapter
import com.edu.group.presentation.bottomsheet.GroupStudentsBottomSheetFragment
import com.edu.group.presentation.model.GroupData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GroupDetailFragment :
    BaseFragment<GroupDetailViewModel, FragmentGroupDetailBinding>(
        R.layout.fragment_group_detail,
        FragmentGroupDetailBinding::bind
    ) {

    override val viewModel: GroupDetailViewModel by viewModels()
    private var groupName: String = ""
    private val args: GroupDetailFragmentArgs by navArgs()

    @Inject
    lateinit var imageLoader: IImageLoader

    private val adapter: GroupTestsAdapter = GroupTestsAdapter {
        findNavController()
    }

    override fun setupUI() {
        viewModel.fetchGroupDetails(args.groupId)
        binding.toolbar.menu.findItem(R.id.edit).isVisible = false
        binding.toolbar.setupWithNavController(findNavController())

        binding.students.setOnClickListener {
            GroupStudentsBottomSheetFragment(args.groupId, viewModel, imageLoader).show(
                parentFragmentManager,
                GroupStudentsBottomSheetFragment.TAG
            )
        }

        binding.btnGroupLeaderBoard.setOnClickListener {
            findNavController().navigate(
                GroupDetailFragmentDirections.actionGroupDetailFragmentToGroupStudentsRatingFragment(
                    args.groupId
                )
            )
        }

        binding.testLayout.setOnClickListener {
            val pair = Navigation.Tests.navigateToTests(args.groupId)
            findNavController().navigate(pair.first, pair.second)
        }
        binding.toolbar.menu.findItem(R.id.edit).setOnMenuItemClickListener {
            findNavController().navigate(
                GroupDetailFragmentDirections.actionGroupDetailFragmentToGroupDetailEditFragment(
                    args.groupId
                )
            )
            true
        }
        setAdapter()
    }

    override fun setupVM() {
        super.setupVM()
        viewModel.groupDetailsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResourceState.Success -> {
                    bindGroupDetailsData(state.data)
                }
                is ResourceState.Error -> {
                    showToast(state.message)
                }
                else -> Unit
            }
        }
    }

    private fun bindGroupDetailsData(data: GroupData) {
        groupName = data.detail.groupName
        binding.title.text = data.detail.groupName
        binding.testsCount.text = data.detail.testsCount.toString()
        binding.status.text = if (data.role == CoreRoleEnum.TEACHER) {
            resources.getString(R.string.you_are_admin)
        } else {
            resources.getString(R.string.you_are_student)
        }
        imageLoader.loadImageWithCircleShape(data.detail.avatar, binding.groupLogo)
        adapter.submitTests(data.tests)
        binding.status.isVisible =
            data.role == CoreRoleEnum.STUDENT || data.role == CoreRoleEnum.TEACHER
        binding.toolbar.menu.findItem(R.id.edit).isVisible = data.role == CoreRoleEnum.TEACHER

    }

    private fun setAdapter() {

    }

    override fun progressLoader(show: Boolean) {
        binding.progressIndicator.root.isVisible = show
    }
}